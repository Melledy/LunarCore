package emu.lunarcore.game.player.lineup;

import emu.lunarcore.game.player.Player;

public class PlayerTempLineup extends PlayerExtraLineup {
    
    public PlayerTempLineup(Player player) {
        super(player, 0);
    }
    
    @Override
    public void save() {
     // Ignored
    }
    
    @Override
    public void delete() {
        // Ignored
    }
}
