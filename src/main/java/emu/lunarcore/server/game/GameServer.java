package emu.lunarcore.server.game;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

import emu.lunarcore.Config.GameServerConfig;
import emu.lunarcore.LunarCore;
import emu.lunarcore.game.battle.BattleService;
import emu.lunarcore.game.drops.DropService;
import emu.lunarcore.game.gacha.GachaService;
import emu.lunarcore.game.inventory.InventoryService;
import emu.lunarcore.game.player.Player;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import kcp.highway.ChannelConfig;
import kcp.highway.KcpServer;
import lombok.Getter;

public class GameServer extends KcpServer {
    private final GameServerConfig serverConfig;
    private final RegionInfo info;
    private final InetSocketAddress address;

    @Getter
    private final GameServerPacketHandler packetHandler;
    private final Int2ObjectMap<Player> players;

    // Managers
    @Getter private final BattleService battleService;
    @Getter private final DropService dropService;
    @Getter private final InventoryService inventoryService;
    @Getter private final GachaService gachaService;
    
    public GameServer(GameServerConfig serverConfig) {
        // Game Server base
        this.serverConfig = serverConfig;
        this.info = new RegionInfo(this);
        this.address = new InetSocketAddress(serverConfig.bindAddress, serverConfig.getPort());
        this.packetHandler = new GameServerPacketHandler();

        this.players = new Int2ObjectOpenHashMap<>();

        // Setup managers
        this.battleService = new BattleService(this);
        this.dropService = new DropService(this);
        this.inventoryService = new InventoryService(this);
        this.gachaService = new GachaService(this);

        // Hook into shutdown event.
        Runtime.getRuntime().addShutdownHook(new Thread(this::onShutdown));
    }

    public GameServerConfig getServerConfig() {
        return this.serverConfig;
    }

    public void registerPlayer(Player player) {
        synchronized (this.players) {
            this.players.put(player.getUid(), player);
        }
    }
    
    public void deregisterPlayer(Player player) {
        synchronized (this.players) {
            Player check = this.players.get(player.getUid());
            if (check == player) {
                this.players.remove(player.getUid());
            }
        }
    }

    public Player getOnlinePlayerByUid(int uid) {
        synchronized (this.players) {
            return this.players.get(uid);
        }
    }

    public void start() {
        // Setup config and init server
        ChannelConfig channelConfig = new ChannelConfig();
        channelConfig.nodelay(true, this.getServerConfig().getKcpInterval(), 2, true);
        channelConfig.setMtu(1400);
        channelConfig.setSndwnd(256);
        channelConfig.setRcvwnd(256);
        channelConfig.setTimeoutMillis(30 * 1000); // 30s
        channelConfig.setUseConvChannel(true);
        channelConfig.setAckNoDelay(true);

        this.init(new GameServerKcpListener(this), channelConfig, address);

        // Setup region info
        this.info.setUp(true);
        this.info.save();
        LunarCore.getHttpServer().forceRegionListRefresh();

        // Done
        LunarCore.getLogger().info("Game Server started on " + address.getPort());
    }

    private void onShutdown() {
        // Close server socket
        this.stop();
        
        // Set region info
        this.info.setUp(false);
        this.info.save();
        
        // Kick and save all players
        List<Player> list = new ArrayList<>(players.size());
        list.addAll(players.values());
        
        for (Player player : list) {
            player.getSession().close();
        }
    }
}
