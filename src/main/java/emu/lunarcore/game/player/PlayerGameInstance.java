package emu.lunarcore.game.player;

import emu.lunarcore.game.battle.Battle;
import emu.lunarcore.proto.BattleEndStatusOuterClass.BattleEndStatus;
import emu.lunarcore.proto.BattleStatisticsOuterClass.BattleStatistics;
import lombok.Getter;

@Getter
public abstract class PlayerGameInstance {
    private transient Player player;
    
    public void setPlayer(Player player) {
        if (this.player == null) {
            this.player = player;
        }
    }
    
    // Events
    
    /**
     * Called when the player starts a battle while in the instance
     * @param battle
     */
    public abstract void onBattleStart(Battle battle);
    
    /**
     * Called when the player finishes a battle while in the instance
     * @param battle
     * @param result
     * @param stats
     */
    public abstract void onBattleFinish(Battle battle, BattleEndStatus result, BattleStatistics stats);
    
    /**
     * Called when the player leaves the instance
     */
    public abstract void onLeave();
}
