package emu.lunarcore.game.battle.skills;

import java.util.List;

import emu.lunarcore.game.avatar.GameAvatar;
import emu.lunarcore.game.battle.Battle;
import emu.lunarcore.game.scene.entity.EntityProp;
import emu.lunarcore.game.scene.entity.GameEntity;
import emu.lunarcore.proto.MotionInfoOuterClass.MotionInfo;
import lombok.Getter;

@Getter
public class MazeSkillHitProp extends MazeSkillAction {

    @Override
    public void onCast(GameAvatar caster, MotionInfo castPosition) {
        // Skip
    }

    @Override
    public void onAttack(GameAvatar caster, Battle battle) {
        // Skip
    }

    @Override
    public void onAttack(GameAvatar caster, List<? extends GameEntity> entities) {
        for (GameEntity entity : entities) {
            if (entity instanceof EntityProp prop) {
                caster.getScene().destroyProp(prop);
            }
        }
    }

}
