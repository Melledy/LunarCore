package emu.lunarcore.data.config;

import java.util.List;

import emu.lunarcore.data.excel.AvatarExcel;
import emu.lunarcore.game.battle.skills.MazeSkill;
import emu.lunarcore.game.battle.skills.MazeSkillAction;
import emu.lunarcore.game.battle.skills.MazeSkillAddBuff;
import lombok.Getter;

/**
 * The equivalent of the SkillAbilityConfig class in anime game.
 */
public class SkillAbilityInfo {
    private List<AbilityInfo> AbilityList;

    public boolean parse(AvatarExcel avatarExcel) {
        // Init variable
        MazeSkill skill = null;
        
        // Look for MazeSkill
        for (AbilityInfo ability : AbilityList) {
            // Skip if not a maze skill
            if (!ability.getName().contains("MazeSkill")) {
                continue;
            }
            
            // Create maze skill
            skill = new MazeSkill();
            
            // Parse tasks
            for (TaskInfo task : ability.getOnStart()) {
                parseTask(skill, skill.getCastActions(), task);
            }
        }
        
        // Set skill for avatar
        if (skill != null) {
            avatarExcel.setMazeSkill(skill); 
            return true;
        }
        
        return false;
    }
    
    private void parseTask(MazeSkill skill, List<MazeSkillAction> actionList, TaskInfo task) {
        if (task.getType().contains("AddMazeBuff")) {
            actionList.add(new MazeSkillAddBuff(task.getID(), 15));
        } else if (task.getType().contains("CreateSummonUnit")) {
            
        } else if (task.getSuccessTaskList() != null) {
            for (TaskInfo t : task.getSuccessTaskList()) {
                parseTask(skill, skill.getCastActions(), t);
            }
        } else if (task.getOnAttack() != null) {
            if (task.getType().contains("AdventureTriggerAttack")) {
                for (TaskInfo t : task.getOnAttack()) {
                    parseTask(skill, skill.getAttackActions(), t);
                }
            } else if (task.getType().contains("AdventureFireProjectile")) {
                for (TaskInfo t : task.getOnAttack()) {
                    parseTask(skill, skill.getAttackActions(), t);
                }
            }
        }
    }
    
    /**
     * The equivalent of the AbilityConfig class in anime game.
     */
    @Getter
    public class AbilityInfo {
        private String Name;
        private List<TaskInfo> OnStart;
    }
}
