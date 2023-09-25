package emu.lunarcore.game.battle;

import emu.lunarcore.game.player.Player;

public class Battle {
    private final Player player;

    public Battle(Player player) {
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }
}
