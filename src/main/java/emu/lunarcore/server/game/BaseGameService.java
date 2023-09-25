package emu.lunarcore.server.game;

public abstract class BaseGameService {
    private final GameServer server;

    public BaseGameService(GameServer server) {
        this.server = server;
    }

    public GameServer getServer() {
        return server;
    }
}
