package emu.lunarcore.data.config;

import java.util.List;

import emu.lunarcore.data.GameData;
import emu.lunarcore.data.excel.AvatarExcel;
import emu.lunarcore.game.battle.skills.*;
import lombok.Getter;

/**
 *  Original name: SkillAbilityConfig
 */
public class SkillAbilityInfo {
    private List<AbilityInfo> AbilityList;

    public boolean parse(AvatarExcel avatarExcel) {
        // Init variable
        MazeSkill skill = null;
        
        // Look for MazeSkill
        for (AbilityInfo ability : AbilityList) {
            //
            List<MazeSkillAction> actionList = null;
            
            // Skip if not a maze skill
            if (ability.getName().contains("MazeSkill")) {
                skill = new MazeSkill(avatarExcel, 2);
                avatarExcel.setMazeSkill(skill);
                
                actionList = skill.getCastActions();
                
                // Hacky way to check if an avatar can summon with their skill
                var summonUnitExcel = GameData.getSummonUnitExcelMap().get((skill.getId() * 10) + 1);
                if (summonUnitExcel != null && !summonUnitExcel.isIsClient()) {
                    // TODO duration is hardcoded
                    skill.getCastActions().add(new MazeSkillSummonUnit(summonUnitExcel, 20));
                }
            } else if (ability.getName().contains("NormalAtk")) {
                skill = new MazeSkill(avatarExcel, 1);
                avatarExcel.setMazeAttack(skill);
                
                actionList = skill.getAttackActions();
            } else {
                continue;
            }
            
            // Parse tasks
            for (TaskInfo task : ability.getOnStart()) {
                parseTask(skill, actionList, task);
            }
        }
        
        return true;
    }
    
    // "Simple" way to parse maze attacks/skills
    // TODO parse tasks better
    private void parseTask(MazeSkill skill, List<MazeSkillAction> actionList, TaskInfo task) {
        if (task.getType().contains("AddMazeBuff")) {
            // TODO get duration from params if buff duration is dynamic
            actionList.add(new MazeSkillAddBuff(task.getID(), 20));
        } else if (task.getType().contains("RemoveMazeBuff")) {
            actionList.removeIf(action -> action instanceof MazeSkillAddBuff actionAdd && actionAdd.getBuffId() == task.getID());
        } else if (task.getType().contains("AdventureModifyTeamPlayerHP")) {
            // TODO get hp increase value from params
            actionList.add(new MazeSkillModifyHP(15));
        } else if (task.getType().contains("AdventureModifyTeamPlayerSP")) {
            // TODO get sp increase value from params, also handle target alias
            actionList.add(new MazeSkillModifySP(50));
        } else if (task.getType().contains("CreateSummonUnit")) {
            // Ignored
        } else if (task.getSuccessTaskList() != null) {
            for (TaskInfo t : task.getSuccessTaskList()) {
                parseTask(skill, actionList, t);
            }
        } else if (task.getType().contains("AdventureTriggerAttack")) {
            if (task.getOnAttack() != null) {
                for (TaskInfo t : task.getOnAttack()) {
                    parseTask(skill, skill.getAttackActions(), t);
                }
            }
            if (skill.getIndex() == 2) {
                skill.setTriggerBattle(task.isTriggerBattle());
            }
        } else if (task.getType().contains("AdventureFireProjectile")) {
            if (task.getOnProjectileHit() != null) {
                for (TaskInfo t : task.getOnProjectileHit()) {
                    parseTask(skill, skill.getAttackActions(), t);
                }
            }
            if (task.getOnProjectileLifetimeFinish() != null) {
                for (TaskInfo t : task.getOnProjectileLifetimeFinish()) {
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
