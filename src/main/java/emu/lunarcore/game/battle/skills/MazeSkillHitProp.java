package emu.lunarcore.game.battle.skills;

import java.util.List;

import emu.lunarcore.game.avatar.GameAvatar;
import emu.lunarcore.game.scene.entity.EntityProp;
import emu.lunarcore.game.scene.entity.GameEntity;
import lombok.Getter;

@Getter
public class MazeSkillHitProp extends MazeSkillAction {

    @Override
    public void onCastHit(GameAvatar caster, List<? extends GameEntity> entities) {
        for (GameEntity entity : entities) {
            if (entity instanceof EntityProp prop) {
                caster.getScene().destroyProp(prop);
            }
        }
    }

}
