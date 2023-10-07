package emu.lunarcore.game.scene.triggers;

import emu.lunarcore.game.enums.PropState;
import emu.lunarcore.game.enums.PropType;
import emu.lunarcore.game.scene.Scene;
import emu.lunarcore.game.scene.entity.EntityProp;
import emu.lunarcore.game.scene.entity.GameEntity;
import lombok.Getter;

@Getter
public class TriggerOpenTreasureWhenMonsterDie extends PropTrigger {
    private int groupId;
    
    public TriggerOpenTreasureWhenMonsterDie(int groupId) {
        this.groupId = groupId;
    }
    
    @Override
    public PropTriggerType getType() {
        return PropTriggerType.MONSTER_DIE;
    }
    
    @Override
    public boolean shouldRun(int param) {
        return this.groupId == param;
    }
    
    @Override
    public void run(Scene scene) {
        synchronized (scene) {
            for (GameEntity entity : scene.getEntities().values()) {
                if (entity.getGroupId() != this.groupId) {
                    continue;
                }
                
                if (entity instanceof EntityProp prop) {
                    if (prop.getExcel().getPropType() == PropType.PROP_TREASURE_CHEST) {
                        prop.setState(PropState.ChestClosed);
                    }
                }
            }
        }
    }
}
