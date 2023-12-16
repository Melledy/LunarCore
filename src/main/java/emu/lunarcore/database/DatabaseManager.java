package emu.lunarcore.database;

import java.util.stream.Stream;

import emu.lunarcore.util.Utils;
import org.bson.codecs.configuration.CodecRegistries;
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
import dev.morphia.InsertOneOptions;
import dev.morphia.Morphia;
import dev.morphia.annotations.Entity;
import dev.morphia.mapping.Mapper;
import dev.morphia.mapping.MapperOptions;
import dev.morphia.query.filters.Filters;

import emu.lunarcore.Config.DatabaseInfo;
import emu.lunarcore.Config.InternalMongoInfo;
import emu.lunarcore.LunarCore;
import emu.lunarcore.LunarCore.ServerType;
import emu.lunarcore.database.codecs.*;
import lombok.Getter;

@Getter
public final class DatabaseManager {
    @Getter private static MongoServer server;
    private Datastore datastore;

    private static final InsertOneOptions INSERT_OPTIONS = new InsertOneOptions();
    private static final DeleteOptions DELETE_OPTIONS = new DeleteOptions();
    private static final DeleteOptions DELETE_MANY = new DeleteOptions().multi(true);

    public DatabaseManager(DatabaseInfo info, ServerType type) {
        // Variables
        var internalConfig = LunarCore.getConfig().getInternalMongoServer();
        String connectionString = info.getUri();

        // Local mongo server
        if (info.isUseInternal() && Utils.isPortOpen(internalConfig.getAddress(), internalConfig.getPort())) {
            connectionString = startInternalMongoServer(internalConfig);
            LunarCore.getLogger().info("Started local MongoDB server at " + server.getConnectionString());
        }

        // Initialize
        MongoClient mongoClient = MongoClients.create(connectionString);
        
        // Add our custom fastutil codecs
        var codecProvider = CodecRegistries.fromCodecs(
               new IntSetCodec(), new IntListCodec(), new Int2IntMapCodec()
        );

        // Set mapper options
        MapperOptions mapperOptions = MapperOptions.builder()
                .storeEmpties(true)
                .storeNulls(false)
                .codecProvider(codecProvider)
                .build();

        // Create data store.
        datastore = Morphia.createDatastore(mongoClient, info.getCollection(), mapperOptions);

        // Map classes
        var entities = new Reflections(LunarCore.class.getPackageName())
                .getTypesAnnotatedWith(Entity.class)
                .stream()
                .filter(cls -> {
                    Entity e = cls.getAnnotation(Entity.class);
                    return e != null && !e.value().equals(Mapper.IGNORED_FIELDNAME);
                })
                .toList();

        if (type.runDispatch()) {
            // Only map account related entities
            var map = entities.stream().filter(cls -> {
                return cls.getAnnotation(AccountDatabaseOnly.class) != null;
            }).toArray(Class<?>[]::new);

            datastore.getMapper().map(map);
        }
        if (type.runGame()) {
            // Only map game related entities
            var map = entities.stream().filter(cls -> {
                return cls.getAnnotation(AccountDatabaseOnly.class) == null;
            }).toArray(Class<?>[]::new);

            datastore.getMapper().map(map);
        }

        // Ensure indexes
        ensureIndexes();
    }

    public MongoDatabase getDatabase() {
        return getDatastore().getDatabase();
    }

    private void ensureIndexes() {
        try {
            datastore.ensureIndexes();
        } catch (MongoCommandException exception) {
            LunarCore.getLogger().warn("Mongo index error: ", exception);
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

    public <T> T getObjectByField(Class<T> cls, String filter, long value) {
        return getDatastore().find(cls).filter(Filters.eq(filter, value)).first();
    }

    public <T> Stream<T> getObjects(Class<T> cls, String filter, long value) {
        return getDatastore().find(cls).filter(Filters.eq(filter, value)).stream();
    }

    public <T> Stream<T> getObjects(Class<T> cls) {
        return getDatastore().find(cls).stream();
    }

    public <T> void save(T obj) {
        getDatastore().save(obj, INSERT_OPTIONS);
    }

    public <T> boolean delete(T obj) {
        DeleteResult result = getDatastore().delete(obj, DELETE_OPTIONS);
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

    // Internal MongoDB server

    public static String startInternalMongoServer(InternalMongoInfo internalMongo) {
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
}
