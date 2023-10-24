package emu.lunarcore;

import java.util.Set;

import lombok.Getter;

@Getter
public class Config {

    public DatabaseInfo accountDatabase = new DatabaseInfo();
    public DatabaseInfo gameDatabase = new DatabaseInfo();
    public InternalMongoInfo internalMongoServer = new InternalMongoInfo();
    public boolean useSameDatabase = true;

    public KeystoreInfo keystore = new KeystoreInfo();

    public HttpServerConfig httpServer = new HttpServerConfig(443);
    public GameServerConfig gameServer = new GameServerConfig(23301);
    
    public ServerOptions serverOptions = new ServerOptions();
    public LogOptions logOptions = new LogOptions();
    public DownloadData downloadData = new DownloadData();

    public String resourceDir = "./resources";
    public String dataDir = "./data";

    @Getter
    public static class DatabaseInfo {
        public String uri = "mongodb://localhost:27017";
        public String collection = "lunarcore";
        public boolean useInternal = true;
    }

    @Getter
    public static class InternalMongoInfo {
        public String address = "localhost";
        public int port = 27017;
        public String filePath = "database.mv";
    }

    @Getter
    public static class KeystoreInfo {
        public String path = "./keystore.p12";
        public String password = "lunar";
    }

    @Getter
    private static class ServerConfig {
        public String bindAddress = "0.0.0.0";
        public String publicAddress = "127.0.0.1";
        public int port;

        public ServerConfig(int port) {
            this.port = port;
        }
    }
    
    @Getter
    public static class HttpServerConfig extends ServerConfig {
        public boolean useSSL = true;
        public long regionListRefresh = 60_000; // Time in milliseconds to wait before refreshing region list cache again

        public HttpServerConfig(int port) {
            super(port);
        }
        
        public String getDisplayAddress() {
            return (useSSL ? "https" : "http") + "://" + publicAddress + ":" + port;
        }
    }

    @Getter
    public static class GameServerConfig extends ServerConfig {
        public String id = "lunar_rail_test";
        public String name = "Test";
        public String description = "Test Server";
        public int kcpInterval = 40;

        public GameServerConfig(int port) {
            super(port);
        }
    }
    
    @Getter
    public static class ServerOptions {
        public int entitySceneLimit = 2000;
        public Set<String> defaultPermissions = Set.of("*");
    }
    
    @Getter
    public static class LogOptions {
        public boolean commands = true;
        public boolean connections = true;
        public boolean packets = false;
    }
    
    @Getter
    public static class DownloadData {
        public String assetBundleUrl = null;
        public String exResourceUrl = null;
        public String luaUrl = null;
        public String ifixUrl = null;
    }

}
