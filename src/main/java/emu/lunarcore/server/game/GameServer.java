package emu.lunarcore.server.game;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

import emu.lunarcore.Config.GameServerConfig;
import emu.lunarcore.LunarRail;
import emu.lunarcore.game.battle.BattleService;
import emu.lunarcore.game.challenge.ChallengeService;
import emu.lunarcore.game.gacha.GachaService;
import emu.lunarcore.game.player.Player;
import emu.lunarcore.game.service.ChatService;
import emu.lunarcore.game.service.InventoryService;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMaps;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import kcp.highway.ChannelConfig;
import kcp.highway.KcpServer;
import lombok.Getter;

public class GameServer extends KcpServer {
    private final GameServerConfig serverConfig;
    private final RegionInfo info;
    private final InetSocketAddress address;

    private final GameServerPacketHandler packetHandler;
    private final Int2ObjectMap<Player> players;

    // Managers
    @Getter private final BattleService battleService;
    @Getter private final InventoryService inventoryService;
    @Getter private final GachaService gachaService;
    @Getter private final ChatService chatService;
    @Getter private final ChallengeService challengeService;
    
    public GameServer(GameServerConfig serverConfig) {
        // Game Server base
        this.serverConfig = serverConfig;
        this.info = new RegionInfo(this);
        this.address = new InetSocketAddress(serverConfig.bindAddress, serverConfig.getPort());
        this.packetHandler = new GameServerPacketHandler();

        this.players = Int2ObjectMaps.synchronize(new Int2ObjectOpenHashMap<>());

        // Setup managers
        this.battleService = new BattleService(this);
        this.inventoryService = new InventoryService(this);
        this.gachaService = new GachaService(this);
        this.chatService = new ChatService(this);
        this.challengeService = new ChallengeService(this);

        // Hook into shutdown event.
        Runtime.getRuntime().addShutdownHook(new Thread(this::onShutdown));
    }

    public GameServerConfig getServerConfig() {
        return this.serverConfig;
    }

    public GameServerPacketHandler getPacketHandler() {
        return this.packetHandler;
    }

    public void registerPlayer(Player player) {
        players.put(player.getUid(), player);
    }

    public void deregisterPlayer(int uid) {
        players.remove(uid);
    }

    public Player getPlayerByUid(int uid) {
        return players.get(uid);
    }

    public void start() {
        // Setup config and init server
        ChannelConfig channelConfig = new ChannelConfig();
        channelConfig.nodelay(true, 50, 2, true);
        channelConfig.setMtu(1400);
        channelConfig.setSndwnd(256);
        channelConfig.setRcvwnd(256);
        channelConfig.setTimeoutMillis(30 * 1000);//30s
        channelConfig.setUseConvChannel(true);
        channelConfig.setAckNoDelay(true);

        this.init(new GameServerKcpListener(this), channelConfig, address);

        // Setup region info
        this.info.setUp(true);
        this.info.save();

        // Done
        LunarRail.getLogger().info("Game Server started on " + address.getPort());
    }

    private void onShutdown() {
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
