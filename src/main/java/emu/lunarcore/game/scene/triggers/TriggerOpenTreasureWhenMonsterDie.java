package emu.lunarcore.game.scene.triggers;

import emu.lunarcore.game.enums.PropState;
import emu.lunarcore.game.enums.PropType;
import emu.lunarcore.game.scene.Scene;
import emu.lunarcore.game.scene.entity.EntityProp;
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
    public boolean shouldRun(int groupId, int instId) {
        return this.groupId == groupId;
    }
    
    @Override
    public void run(Scene scene) {
        // Open trigger
        for (var prop : scene.getEntitiesByGroup(EntityProp.class, this.getGroupId())) {
            if (prop.getExcel().getPropType() == PropType.PROP_TREASURE_CHEST) {
                prop.setState(PropState.ChestClosed);
            }
        }
    }
}
