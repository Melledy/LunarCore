package emu.lunarcore.game.battle.skills;

import java.util.List;

import emu.lunarcore.game.avatar.GameAvatar;
import emu.lunarcore.game.battle.Battle;
import emu.lunarcore.game.scene.entity.GameEntity;
import emu.lunarcore.proto.MotionInfoOuterClass.MotionInfo;

public abstract class MazeSkillAction {
    
    public abstract void onCast(GameAvatar caster, MotionInfo castPosition);
    
    public abstract void onAttack(GameAvatar caster, Battle battle);
    
    public abstract void onAttack(GameAvatar caster, List<? extends GameEntity> entities);
    
}
