package emu.lunarcore.game.player;

public abstract class BasePlayerManager {
    private transient Player player;

    public BasePlayerManager(Player player) {
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }
}
