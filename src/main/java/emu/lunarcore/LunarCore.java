package emu.lunarcore;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

import emu.lunarcore.plugin.PluginManager;
import org.jline.reader.EndOfFileException;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.UserInterruptException;
import org.jline.reader.impl.LineReaderImpl;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import emu.lunarcore.command.CommandManager;
import emu.lunarcore.data.ResourceLoader;
import emu.lunarcore.database.DatabaseManager;
import emu.lunarcore.server.game.GameServer;
import emu.lunarcore.server.http.HttpServer;
import emu.lunarcore.util.Handbook;
import emu.lunarcore.util.JsonUtils;
import lombok.Getter;

public class LunarCore {
    private static final Logger log = LoggerFactory.getLogger(LunarCore.class);
    private static File configFile = new File("./config.json");
    @Getter private static Config config;

    @Getter private static DatabaseManager accountDatabase;
    @Getter private static DatabaseManager gameDatabase;

    @Getter private static HttpServer httpServer;
    @Getter private static GameServer gameServer;

    @Getter private static CommandManager commandManager;
    @Getter private static PluginManager pluginManager;
    @Getter private static ServerType serverType = ServerType.BOTH;

    private static LineReaderImpl reader;
    @Getter private static boolean usingDumbTerminal;
    
    private static long timeOffset = 0;

    static {
        // Setup console reader
        try {
            reader = (LineReaderImpl) LineReaderBuilder.builder()
                    .terminal(TerminalBuilder.builder().dumb(true).build())
                    .build();

            usingDumbTerminal = Terminal.TYPE_DUMB.equals(reader.getTerminal().getType());
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Load config
        LunarCore.loadConfig();
        LunarCore.updateServerTimeOffset();
    }

    public static void main(String[] args) {
        // Start Server
        LunarCore.getLogger().info("Starting Lunar Core " + getJarVersion());
        LunarCore.getLogger().info("Git hash: " + getGitHash());
        LunarCore.getLogger().info("Game version: " + GameConstants.VERSION);
        boolean generateHandbook = true;

        // Load commands
        LunarCore.commandManager = new CommandManager();
        
        // Load plugin manager
        LunarCore.pluginManager = new PluginManager();

        try {
            LunarCore.getPluginManager().loadPlugins();
        } catch (Exception exception) {
            LunarCore.getLogger().error("Unable to load plugins.", exception);
        }

        // Parse arguments
        for (String arg : args) {
            switch (arg) {
            case "-dispatch":
                serverType = ServerType.DISPATCH;
                break;
            case "-game":
                serverType = ServerType.GAME;
                break;
            case "-nohandbook":
            case "-skiphandbook":
                generateHandbook = false;
                break;
            case "-database":
                // Database only
                DatabaseManager.startInternalMongoServer(LunarCore.getConfig().getInternalMongoServer());
                LunarCore.getLogger().info("Running local mongo server at " + DatabaseManager.getServer().getConnectionString());
                // Console
                LunarCore.startConsole();
                return;
            }
        }

        // Skip these if we are only running the http server in dispatch mode
        if (serverType.runGame()) {
            // Load resources
            ResourceLoader.loadAll();

            // Build handbook
            if (generateHandbook) {
                Handbook.generate();
            }
        }

        try {
            // Start Database(s)
            LunarCore.initDatabases();
        } catch (Exception exception) {
            LunarCore.getLogger().error("Unable to start the database(s).", exception);
        }

        try {
            // Always run http server as it is needed by for dispatch and gateserver
            httpServer = new HttpServer(serverType);
            httpServer.start();
        } catch (Exception exception) {
            LunarCore.getLogger().error("Unable to start the HTTP server.", exception);
        }

        // Start game server
        if (serverType.runGame()) try {
            gameServer = new GameServer(getConfig().getGameServer());
            gameServer.start();
        } catch (Exception exception) {
            LunarCore.getLogger().error("Unable to start the game server.", exception);
        }
        
        // Hook into shutdown event
        Runtime.getRuntime().addShutdownHook(new Thread(LunarCore::onShutdown));

        // Enable plugins
        LunarCore.getPluginManager().enablePlugins();

        // Start console
        LunarCore.startConsole();
    }

    public static Logger getLogger() {
        return log;
    }

    public static LineReaderImpl getLineReader() {
        return reader;
    }

    // Database

    private static void initDatabases() {
        if (LunarCore.getConfig().useSameDatabase) {
            // Setup account and game database
            accountDatabase = new DatabaseManager(LunarCore.getConfig().getAccountDatabase(), serverType);
            // Optimization: Dont run a 2nd database manager if we are not running a gameserver
            if (serverType.runGame()) {
                gameDatabase = accountDatabase;
            }
        } else {
            // Run separate databases
            accountDatabase = new DatabaseManager(LunarCore.getConfig().getAccountDatabase(), ServerType.DISPATCH);
            // Optimization: Dont run a 2nd database manager if we are not running a gameserver
            if (serverType.runGame()) {
                gameDatabase = new DatabaseManager(LunarCore.getConfig().getGameDatabase(), ServerType.GAME);
            }
        }
    }

    // Config

    public static void loadConfig() {
        // Load from file
        try (FileReader file = new FileReader(configFile)) {
            LunarCore.config = JsonUtils.loadToClass(file, Config.class);
        } catch (Exception e) {
            // Ignored
        }
        
        // Sanity check
        if (LunarCore.getConfig() == null) {
            LunarCore.config = new Config();
        }
        
        // Save config
        LunarCore.saveConfig();
    }

    public static void saveConfig() {
        try (FileWriter file = new FileWriter(configFile)) {
            Gson gson = new GsonBuilder()
                    .setDateFormat("dd-MM-yyyy hh:mm:ss")
                    .setPrettyPrinting()
                    .serializeNulls()
                    .create();
            
            file.write(gson.toJson(config));
        } catch (Exception e) {
            getLogger().error("Config save error");
        }
    }

    // Build Config
    
    private static String getJarVersion() {
        // Safely get the build config class without errors even if it hasnt been generated yet
        try {
            Class<?> buildConfig = Class.forName(LunarCore.class.getPackageName() + ".BuildConfig");
            return buildConfig.getField("VERSION").get(null).toString();
        } catch (Exception e) {
            // Ignored
        }
        
        return "";
    }

    private static String getGitHash() {
        // Use a string builder in case one of the build config fields are missing
        StringBuilder builder = new StringBuilder();
        
        // Safely get the build config class without errors even if it hasnt been generated yet
        try {
            SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Class<?> buildConfig = Class.forName(LunarCore.class.getPackageName() + ".BuildConfig");
            
            String hash = buildConfig.getField("GIT_HASH").get(null).toString();
            builder.append(hash);
            
            String timestamp = buildConfig.getField("GIT_TIMESTAMP").get(null).toString();
            long time = Long.parseLong(timestamp) * 1000;
            builder.append(" (" + sf.format(new Date(time)) + ")");
        } catch (Exception e) {
            // Ignored
        }
        
        if (builder.isEmpty()) {
            return "";
        } else {
            return builder.toString();
        }
    }
    
    /**
     * Returns the current server's time in milliseconds to send to the client. Can be used to spoof server time.
     */
    public static long currentServerTime() {
        return convertToServerTime(System.currentTimeMillis());
    }
    
    /**
     * Converts a timestamp (in milliseconds) to the server time
     */
    public static long convertToServerTime(long time) {
        return time + timeOffset;
    }
    
    private static void updateServerTimeOffset() {
        var timeOptions = LunarCore.getConfig().getServerTime();
        if (timeOptions.isSpoofTime() && timeOptions.getSpoofDate() != null) {
            timeOffset = timeOptions.getSpoofDate().getTime() - System.currentTimeMillis();
        } else {
            timeOffset = 0;
        }
    }

    // Server console

    private static void startConsole() {
        try {
            while (true) {
                String input = reader.readLine("> ");
                if (input == null || input.length() == 0) {
                    continue;
                }

                LunarCore.getCommandManager().invoke(null, input);
            }
        } catch (UserInterruptException | EndOfFileException e) {
            // CTRL + C / CTRL + D
            System.exit(0);
        } catch (Exception e) {
            LunarCore.getLogger().error("Terminal error: ", e);
        }
    }

    // Shutdown event

    private static void onShutdown() {
        if (gameServer != null) {
            gameServer.onShutdown();
        }

        if (pluginManager != null) {
            pluginManager.disablePlugins();
        }
    }

    // Server enums

    public enum ServerType {
        DISPATCH    (0x1),
        GAME        (0x2),
        BOTH        (0x3);

        private final int flags;

        ServerType(int flags) {
            this.flags = flags;
        }

        public boolean runDispatch() {
            return (this.flags & 0x1) == 0x1;
        }

        public boolean runGame() {
            return (this.flags & 0x2) == 0x2;
        }
    }

    // Hiro was here
}
