package emu.lunarcore.game.battle.skills;

import java.util.List;

import emu.lunarcore.game.avatar.GameAvatar;
import emu.lunarcore.game.battle.Battle;
import emu.lunarcore.game.scene.entity.GameEntity;
import emu.lunarcore.proto.MotionInfoOuterClass.MotionInfo;

public class MazeSkillModifyHP extends MazeSkillAction {
    private int amount;
    
    public MazeSkillModifyHP(int hp) {
        this.amount = hp * 100;
    }

    @Override
    public void onCast(GameAvatar caster, MotionInfo castPosition) {
        caster.getOwner().getCurrentLineup().heal(this.amount, false);
    }

    @Override
    public void onAttack(GameAvatar caster, Battle battle) {
        
    }

    @Override
    public void onAttack(GameAvatar caster, List<? extends GameEntity> entities) {
        
    }

}
