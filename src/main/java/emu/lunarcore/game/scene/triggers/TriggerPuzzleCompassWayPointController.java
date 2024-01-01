package emu.lunarcore.game.scene.triggers;

import emu.lunarcore.game.enums.PropState;
import emu.lunarcore.game.scene.Scene;
import emu.lunarcore.game.scene.entity.EntityProp;
import lombok.Getter;

@Getter
public class TriggerPuzzleCompassWayPointController extends PropTrigger {
    private int groupId;
    
    public TriggerPuzzleCompassWayPointController(int groupId) {
        this.groupId = groupId;
    }
    
    @Override
    public PropTriggerType getType() {
        return PropTriggerType.PUZZLE_FINISH;
    }
    
    @Override
    public boolean shouldRun(int groupId, int instId) {
        return this.groupId == groupId;
    }
    
    @Override
    public void run(Scene scene) {
        for (var prop : scene.getEntitiesByGroup(EntityProp.class, groupId)) {
            prop.setState(PropState.Open);
        }
    }
}
