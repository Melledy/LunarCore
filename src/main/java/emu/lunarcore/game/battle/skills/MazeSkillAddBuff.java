package emu.lunarcore.game.battle.skills;

import emu.lunarcore.game.avatar.GameAvatar;
import emu.lunarcore.game.battle.Battle;
import emu.lunarcore.proto.MotionInfoOuterClass.MotionInfo;
import lombok.Getter;

@Getter
public class MazeSkillAddBuff extends MazeSkillAction {
    private int buffId;
    private int duration;
    
    public MazeSkillAddBuff(int buffId, int duration) {
        this.buffId = buffId;
        this.duration = duration;
    }
    
    @Override
    public void onCast(GameAvatar caster, MotionInfo castPosition) {
        caster.addBuff(buffId, duration);
    }
    
    @Override
    public void onAttack(GameAvatar caster, Battle battle) {
        // Get amount of monster waves in battle
        int waveCount = battle.getMonsterWaveCount();
        // Add buff for each wave id
        for (int i = 0; i < waveCount; i++) {
            battle.addBuff(buffId, caster.getOwner().getLineupManager().getCurrentLeader(), 1 << i);
        }
    }
    
}
