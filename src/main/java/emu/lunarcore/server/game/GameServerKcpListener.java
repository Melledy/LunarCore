package emu.lunarcore.server.game;

import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMaps;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import kcp.highway.KcpListener;
import kcp.highway.Ukcp;

public class GameServerKcpListener implements KcpListener {
    private final GameServer server;
    private final Object2ObjectMap<Ukcp, GameSession> sessions;

    public GameServerKcpListener(GameServer server) {
        this.server = server;
        this.sessions = Object2ObjectMaps.synchronize(new Object2ObjectOpenHashMap<>());
    }

    public GameServer getServer() {
        return this.server;
    }

    @Override
    public void onConnected(Ukcp ukcp) {
        GameSession session = new GameSession(server, ukcp);
        sessions.put(ukcp, session);
        session.onConnect();
    }

    @Override
    public void handleClose(Ukcp ukcp) {
        GameSession session = sessions.remove(ukcp);

        if (session != null) {
            session.onDisconnect();
        }
    }

    @Override
    public void handleReceive(ByteBuf packet, Ukcp ukcp) {
        GameSession session = sessions.get(ukcp);

        if (session != null) {
            session.onMessage(packet);
        }
    }

    @Override
    public void handleException(Throwable err, Ukcp ukcp) {
        
    }
}
