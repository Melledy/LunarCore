package emu.lunarcore.data.config;

import java.util.ArrayList;
import java.util.List;

import emu.lunarcore.game.battle.skills.MazeSkillAction;
import emu.lunarcore.game.battle.skills.MazeSkillAddBuff;
import emu.lunarcore.game.battle.skills.MazeSkillHitProp;
import lombok.Getter;

/**
 * Original name: SummonUnitConfig
 */
@Getter
public class SummonUnitInfo {
    private String AttachPoint;
    private SummonUnitTriggers TriggerConfig;
    
    public List<SummonUnitCustomTrigger> getCustomTriggers() {
        return TriggerConfig.getCustomTriggers();
    }
    
    public SummonUnitCustomTrigger getTriggerByName(String name) {
        return getCustomTriggers().stream()
                .filter(c -> c.getTriggerName().equals(name))
                .findFirst()
                .orElse(null);
    }
    
    public void buildMazeSkillActions() {
        for (var customTrigger : getCustomTriggers()) {
            customTrigger.buildMazeSkillActions();
        }
    }
    
    /**
     * Original name: SummonUnitTriggerConfig
     */
    @Getter
    public static class SummonUnitTriggers {
        private List<SummonUnitCustomTrigger> CustomTriggers;
    }
    
    /**
     * Original name: UnitCustomTriggerConfig
     */
    @Getter
    public static class SummonUnitCustomTrigger {
        private String TriggerName;
        private List<TaskInfo> OnTriggerEnter;
        
        private transient List<MazeSkillAction> actions;
        
        public void buildMazeSkillActions() {
            // Create actions list
            this.actions = new ArrayList<>();
            
            // Sanity check
            if (this.OnTriggerEnter == null) return;
            
            // Build maze actions
            for (var task : this.OnTriggerEnter) {
                if (task.getType().contains("AddMazeBuff")) {
                    // TODO get duration from params if buff duration is dynamic
                    var actionAddBuff = new MazeSkillAddBuff(task.getID(), 5);
                    actionAddBuff.setSendBuffPacket(true);
                    
                    actions.add(actionAddBuff);
                } else if (task.getType().contains("TriggerHitProp")) {
                    actions.add(new MazeSkillHitProp());
                }
            }
        }
    }

}
