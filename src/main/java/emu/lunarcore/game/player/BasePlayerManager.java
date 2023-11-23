package emu.lunarcore.game.player;

import emu.lunarcore.server.game.GameServer;

public abstract class BasePlayerManager {
    private transient Player player;

    public BasePlayerManager(Player player) {
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }
    
    public GameServer getServer() {
        return player.getServer();
    }
}
