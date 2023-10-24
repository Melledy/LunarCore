package emu.lunarcore.game.scene.triggers;

import emu.lunarcore.game.enums.PropState;
import emu.lunarcore.game.scene.Scene;
import emu.lunarcore.game.scene.entity.EntityProp;
import emu.lunarcore.game.scene.entity.GameEntity;
import emu.lunarcore.util.Utils;
import lombok.Getter;

@Getter
public class TriggerPuzzleCompassWayPointController extends PropTrigger {
    private int groupId;
    private int puzzleInstId;
    private int chestInstId;
    
    public TriggerPuzzleCompassWayPointController(String compassKey, String chestKey) {
        String[] compass = compassKey.split(",");
        String[] chest = chestKey.split(",");
        
        this.groupId = Utils.parseSafeInt(compass[0]);
        this.puzzleInstId = Utils.parseSafeInt(compass[1]);
        this.chestInstId = Utils.parseSafeInt(chest[1]);
    }
    
    @Override
    public PropTriggerType getType() {
        return PropTriggerType.PUZZLE_FINISH;
    }
    
    @Override
    public boolean shouldRun(int groupId, int instId) {
        return this.groupId == groupId && this.puzzleInstId == instId;
    }
    
    @Override
    public void run(Scene scene) {
        synchronized (scene) {
            for (GameEntity entity : scene.getEntities().values()) {
                if (entity.getGroupId() != this.groupId) {
                    continue;
                }
                
                if (entity instanceof EntityProp prop) {
                    if (prop.getInstId() == chestInstId) {
                        prop.setState(PropState.ChestClosed);
                    }
                }
            }
        }
    }
}
