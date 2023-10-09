package emu.lunarcore;

import java.io.*;

import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import ch.qos.logback.classic.Logger;
import emu.lunarcore.command.CommandManager;
import emu.lunarcore.data.ResourceLoader;
import emu.lunarcore.database.DatabaseManager;
import emu.lunarcore.server.game.GameServer;
import emu.lunarcore.server.http.HttpServer;
import emu.lunarcore.util.Handbook;
import emu.lunarcore.util.JsonUtils;
import lombok.Getter;

public class LunarRail {
    private static Logger log = (Logger) LoggerFactory.getLogger(LunarRail.class);
    private static File configFile = new File("./config.json");
    private static Config config;

    @Getter private static DatabaseManager accountDatabase;
    @Getter private static DatabaseManager gameDatabase;

    @Getter private static HttpServer httpServer;
    @Getter private static GameServer gameServer;

    @Getter private static CommandManager commandManager;
    
    private static ServerType serverType = ServerType.BOTH;

    // Load config first before doing anything
    static {
        LunarRail.loadConfig();
    }

    public static void main(String[] args) {
        // Start Server
        LunarRail.getLogger().info("Starting Lunar Rail...");
        
        // Load commands
        LunarRail.commandManager = new CommandManager();

        // Parse arguments
        for (String arg : args) {
            switch (arg) {
            case "-dispatch":
                serverType = ServerType.DISPATCH;
                break;
            case "-game":
                serverType = ServerType.GAME;
                break;
            case "-database":
                // Database only
                DatabaseManager databaseManager = new DatabaseManager();
                databaseManager.startInternalMongoServer(LunarRail.getConfig().getInternalMongoServer());
                LunarRail.getLogger().info("Running local mongo server at " + databaseManager.getServer().getConnectionString());
                // Console
                LunarRail.startConsole();
                return;
            }
        }
        
        // Load resources
        ResourceLoader.loadAll();

        // Build handbook TODO
        Handbook.generate();

        // Start Database(s)
        LunarRail.initDatabases();

        // Start Servers TODO
        httpServer = new HttpServer(serverType);
        httpServer.start();

        if (serverType.runGame()) {
            gameServer = new GameServer(getConfig().getGameServer());
            gameServer.start();
        }

        // Start console
        LunarRail.startConsole();
    }

    public static Config getConfig() {
        return config;
    }

    public static Logger getLogger() {
        return log;
    }

    // Database

    private static void initDatabases() {
        accountDatabase = new DatabaseManager(LunarRail.getConfig().getAccountDatabase());

        if (LunarRail.getConfig().useSameDatabase) {
            gameDatabase = accountDatabase;
        } else {
            gameDatabase = new DatabaseManager(LunarRail.getConfig().getGameDatabase());
        }
    }

    // Config

    public static void loadConfig() {
        try (FileReader file = new FileReader(configFile)) {
            config = JsonUtils.loadToClass(file, Config.class);
        } catch (Exception e) {
            LunarRail.config = new Config();
        }
        saveConfig();
    }

    public static void saveConfig() {
        try (FileWriter file = new FileWriter(configFile)) {
            Gson gson = new GsonBuilder().setPrettyPrinting().serializeNulls().create();
            file.write(gson.toJson(config));
        } catch (Exception e) {
            getLogger().error("Config save error");
        }
    }

    // Server console

    private static void startConsole() {
        String input;
        try (BufferedReader br = new BufferedReader(new InputStreamReader(System.in))) {
            while ((input = br.readLine()) != null) {
                LunarRail.getCommandManager().invoke(null, input);
            }
        } catch (Exception e) {
            LunarRail.getLogger().error("Console error:", e);
        }
    }

    // Server enums

    public enum ServerType {
        BOTH        (true, true),
        DISPATCH    (true, false),
        GAME        (false, true);

        private final boolean runDispatch;
        private final boolean runGame;

        private ServerType(boolean runDispatch, boolean runGame) {
            this.runDispatch = runDispatch;
            this.runGame = runGame;
        }

        public boolean runDispatch() {
            return runDispatch;
        }

        public boolean runGame() {
            return runGame;
        }
    }
}
