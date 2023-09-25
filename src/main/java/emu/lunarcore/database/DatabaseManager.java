package emu.lunarcore.database;

import java.util.stream.Stream;

import org.reflections.Reflections;

import com.mongodb.MongoCommandException;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoIterable;
import com.mongodb.client.result.DeleteResult;

import de.bwaldvogel.mongo.MongoBackend;
import de.bwaldvogel.mongo.MongoServer;
import de.bwaldvogel.mongo.backend.h2.H2Backend;
import de.bwaldvogel.mongo.backend.memory.MemoryBackend;
import dev.morphia.Datastore;
import dev.morphia.DeleteOptions;
import dev.morphia.Morphia;
import dev.morphia.annotations.Entity;
import dev.morphia.mapping.Mapper;
import dev.morphia.mapping.MapperOptions;
import dev.morphia.query.filters.Filters;
import emu.lunarcore.Config.DatabaseInfo;
import emu.lunarcore.Config.InternalMongoInfo;
import emu.lunarcore.LunarRail;

public final class DatabaseManager {
    private MongoServer server;
    private Datastore datastore;
    private DeleteOptions DELETE_MANY = new DeleteOptions().multi(true);

    public DatabaseManager() {

    }

    public DatabaseManager(DatabaseInfo info) {
        // Variables
        String connectionString = info.getUri();

        // Local mongo server
        if (info.isUseInternal()) {
            connectionString = startInternalMongoServer(LunarRail.getConfig().getInternalMongoServer());
            LunarRail.getLogger().info("Using local mongo server at " + server.getConnectionString());
        }

        // Initialize
        MongoClient gameMongoClient = MongoClients.create(connectionString);

        // Set mapper options.
        MapperOptions mapperOptions = MapperOptions.builder()
                .storeEmpties(true)
                .storeNulls(false)
                .build();

        // Create data store.
        datastore = Morphia.createDatastore(gameMongoClient, info.getCollection(), mapperOptions);

        // Map classes
        Class<?>[] entities = new Reflections(LunarRail.class.getPackageName())
                .getTypesAnnotatedWith(Entity.class)
                .stream()
                .filter(cls -> {
                    Entity e = cls.getAnnotation(Entity.class);
                    return e != null && !e.value().equals(Mapper.IGNORED_FIELDNAME);
                })
                .toArray(Class<?>[]::new);

        datastore.getMapper().map(entities);

        // Ensure indexes
        ensureIndexes();
    }

    public MongoServer getServer() {
        return server;
    }

    public MongoDatabase getDatabase() {
        return getDatastore().getDatabase();
    }

    public Datastore getDatastore() {
        return datastore;
    }

    private void ensureIndexes() {
        try {
            datastore.ensureIndexes();
        } catch (MongoCommandException exception) {
            LunarRail.getLogger().warn("Mongo index error: ", exception);
            // Duplicate index error
            if (exception.getCode() == 85) {
                // Drop all indexes and re add them
                MongoIterable<String> collections = datastore.getDatabase().listCollectionNames();
                for (String name : collections) {
                    datastore.getDatabase().getCollection(name).dropIndexes();
                }
                // Add back indexes
                datastore.ensureIndexes();
            }
        }
    }

    //

    public String startInternalMongoServer(InternalMongoInfo internalMongo) {
        // Get backend
        MongoBackend backend = null;

        if (internalMongo.filePath != null && internalMongo.filePath.length() > 0) {
            backend = new H2Backend(internalMongo.filePath);
        } else {
            backend = new MemoryBackend();
        }

        // Create the local mongo server and replace the connection string
        server = new MongoServer(backend);

        // Bind to address of it exists
        if (internalMongo.getAddress() != null && internalMongo.getPort() != 0) {
            server.bind(internalMongo.getAddress(), internalMongo.getPort());
        } else {
            server.bind(); // Binds to random port
        }

        return server.getConnectionString();
    }

    // Database Functions

    public boolean checkIfObjectExists(Class<?> cls, long uid) {
        return getDatastore().find(cls).filter(Filters.eq("_id", uid)).count() > 0;
    }

    public <T> T getObjectByUid(Class<T> cls, long uid) {
        return getDatastore().find(cls).filter(Filters.eq("_id", uid)).first();
    }

    public <T> T getObjectByField(Class<T> cls, String filter, String value) {
        return getDatastore().find(cls).filter(Filters.eq(filter, value)).first();
    }

    public <T> T getObjectByField(Class<T> cls, String filter, int value) {
        return getDatastore().find(cls).filter(Filters.eq(filter, value)).first();
    }

    public <T> Stream<T> getObjects(Class<T> cls, String filter, long uid) {
        return getDatastore().find(cls).filter(Filters.eq(filter, uid)).stream();
    }

    public <T> Stream<T> getObjects(Class<T> cls) {
        return getDatastore().find(cls).stream();
    }

    public <T> void save(T obj) {
        getDatastore().save(obj);
    }

    public <T> boolean delete(T obj) {
        DeleteResult result = getDatastore().delete(obj);
        return result.getDeletedCount() > 0;
    }

    public boolean delete(Class<?> cls, String filter, long uid) {
        DeleteResult result = getDatastore().find(cls).filter(Filters.eq(filter, uid)).delete(DELETE_MANY);
        return result.getDeletedCount() > 0;
    }

    public synchronized int getNextObjectId(Class<?> c) {
        DatabaseCounter counter = getDatastore().find(DatabaseCounter.class).filter(Filters.eq("_id", c.getSimpleName())).first();
        if (counter == null) {
            counter = new DatabaseCounter(c.getSimpleName());
        }
        try {
            return counter.getNextId();
        } finally {
            getDatastore().save(counter);
        }
    }
}
