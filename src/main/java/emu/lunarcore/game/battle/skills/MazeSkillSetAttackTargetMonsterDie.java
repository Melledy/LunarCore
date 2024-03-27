package emu.lunarcore.game.battle.skills;

import java.util.List;

import emu.lunarcore.game.avatar.GameAvatar;
import emu.lunarcore.game.enums.MonsterRank;
import emu.lunarcore.game.inventory.ItemParamMap;
import emu.lunarcore.game.scene.entity.EntityMonster;
import emu.lunarcore.game.scene.entity.GameEntity;
import emu.lunarcore.util.Utils;

public class MazeSkillSetAttackTargetMonsterDie extends MazeSkillAction {
    
    public void onAttack(GameAvatar caster, List<? extends GameEntity> entities) {
        for (var entity : entities) {
            if (entity instanceof EntityMonster monster && monster.getExcel().getRank().getVal() < MonsterRank.Elite.getVal()) {
                // Remove entity
                monster.getScene().removeEntity(monster);
                
                // Handle drops
                var drops = new ItemParamMap();
                monster.calculateDrops(drops);
                caster.getOwner().getInventory().addItems(drops.toItemList(), true);
                
                // Rogue TODO optimize
                if (caster.getOwner().getRogueInstance() != null) {
                    caster.getOwner().getRogueInstance().createBuffSelect(1);
                    caster.getOwner().getRogueInstance().addMoney(Utils.randomRange(20, 40));
                }
            }
        }
    }
    
}
