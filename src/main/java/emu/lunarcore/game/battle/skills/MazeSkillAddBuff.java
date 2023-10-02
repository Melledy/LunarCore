package emu.lunarcore.game.battle.skills;

import emu.lunarcore.game.avatar.GameAvatar;
import emu.lunarcore.game.battle.Battle;
import emu.lunarcore.proto.MotionInfoOuterClass.MotionInfo;
import lombok.AccessLevel;
import lombok.Getter;

@Getter(AccessLevel.PRIVATE)
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
        battle.addBuff(buffId);
    }
    
}
