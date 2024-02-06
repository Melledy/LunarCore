package emu.lunarcore;

import java.util.Date;
import java.util.List;
import java.util.Set;

import com.google.gson.annotations.SerializedName;

import emu.lunarcore.data.common.ItemParam;
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
    public ServerTime serverTime = new ServerTime();
    public ServerRates serverRates = new ServerRates();
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
        @SerializedName(value = "bindPort", alternate = {"port"})
        public int bindPort;
        
        // Will return bindAddress if publicAddress is null
        public String publicAddress = "127.0.0.1";
        // Will return bindPort if publicPort is null
        public Integer publicPort;
        
        public ServerConfig(int port) {
            this.bindPort = port;
        }
        
        public String getPublicAddress() {
            if (this.publicAddress != null && !this.publicAddress.isEmpty()) {
                return this.publicAddress;
            }
            
            return this.bindAddress;
        }
        
        public int getPublicPort() {
            if (this.publicPort != null && this.publicPort != 0) {
                return this.publicPort;
            }
            
            return this.bindPort;
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
            return (useSSL ? "https" : "http") + "://" + getPublicAddress() + ":" + getPublicPort();
        }
    }

    @Getter
    public static class GameServerConfig extends ServerConfig {
        public String id = "lunar_rail_test";
        public String name = "Lunar Core";
        public String description = "A LunarCore server";
        public int kcpInterval = 40;

        public GameServerConfig(int port) {
            super(port);
        }
    }
    
    @Getter 
    public static class ServerTime {
        public boolean spoofTime = false;
        public Date spoofDate = new Date(1705276800000L); // January 15, 2024 12:00:00 AM (GMT)
    }
    
    @Getter
    public static class ServerOptions {
        public boolean autoCreateAccount = true;
        public int sceneMaxEntites = 500;
        public int maxCustomRelicLevel = 15; // Maximum level of a relic that the player can create with the /give command
        public boolean unlockAllChallenges = true;
        public boolean spendStamina = true;
        public int staminaRecoveryRate = 5 * 60;
        public int staminaReserveRecoveryRate = 18 * 60;
        public int startTrailblazerLevel = 1; // Starting trailblazer level for new players
        public boolean autoUpgradeWorldLevel = true; // Automatically upgrades world level when the player reaches a certain TB level
        public String language = "EN";
        public Set<String> defaultPermissions = Set.of("*");
        
        public ServerProfile serverFriendInfo = new ServerProfile();
        public WelcomeMail welcomeMail = new WelcomeMail();
        
        public int getStaminaRecoveryRate() {
            return staminaRecoveryRate > 0 ? staminaRecoveryRate : 1;
        }
        
        public int getStaminaReserveRecoveryRate() {
            return staminaReserveRecoveryRate > 0 ? staminaReserveRecoveryRate : 1;
        }
    }
    
    @Getter
    public static class ServerRates {
        public double exp = 1.0;
        public double credit = 1.0;
        public double jade = 1.0;
        public double material = 1.0;
        public double equip = 1.0;
    }
    
    @Getter
    public static class ServerProfile {
        public String name = "Server";
        public String signature = "Type /help for a list of commands";
        public int level = 1;
        public int headIcon = 201001;
        public int chatBubbleId = 0;
        public int displayAvatarId = 1001;
        public int displayAvatarLevel = 1;
    }
    
    @Getter
    public static class WelcomeMail {
        public String title;
        public String sender;
        public String content;
        public List<ItemParam> attachments;
        
        public WelcomeMail() {
            this.title = "Welcome to a LunarCore server";
            this.sender = "Server";
            this.content = "Welcome to Lunar Core! Please take these items as a starter gift. For a list of commands, type /help in the server chat window. Check out our <a type=OpenURL1 href=https://discord.gg/cfPKJ6N5hw>Discord</a> and <a type=OpenURL1 href=https://github.com/Melledy/LunarCore>Github</a> for more information about the server.";
            this.attachments = List.of(
                new ItemParam(2, 1000000),
                new ItemParam(101, 100),
                new ItemParam(102, 100),
                new ItemParam(1001, 1),
                new ItemParam(1002, 1)
            );
        }
    }
    
    @Getter
    public static class LogOptions {
        public boolean commands = true;
        public boolean connections = true;
        public boolean packets = false;
        public boolean filterLoopingPackets = false;
    }
    
    @Getter
    public static class DownloadData {
        public String assetBundleUrl = null;
        public String exResourceUrl = null;
        public String luaUrl = null;
        public String ifixUrl = null;
    }

}
