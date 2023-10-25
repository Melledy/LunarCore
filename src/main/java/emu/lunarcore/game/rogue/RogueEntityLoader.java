package emu.lunarcore.game.rogue;

import emu.lunarcore.data.GameData;
import emu.lunarcore.data.config.GroupInfo;
import emu.lunarcore.data.config.MonsterInfo;
import emu.lunarcore.data.excel.NpcMonsterExcel;
import emu.lunarcore.data.excel.RogueMonsterExcel;
import emu.lunarcore.game.scene.Scene;
import emu.lunarcore.game.scene.SceneEntityLoader;
import emu.lunarcore.game.scene.entity.EntityMonster;

public class RogueEntityLoader extends SceneEntityLoader {
    
    public EntityMonster loadMonster(Scene scene, GroupInfo group, MonsterInfo monsterInfo) {
        // Make sure player is in a rogue instance
        RogueData rogue = scene.getPlayer().getRogueData();
        if (rogue == null) return null;

        // Get rogue group content
        int content = rogue.getCurrentRoom().getExcel().getGroupContent(group.getId());
        if (content <= 0) return null;
        
        // Get rogue monster excel and npc monster excel
        RogueMonsterExcel rogueMonster = GameData.getRogueMonsterExcelMap().get((content * 10) + 1);
        if (rogueMonster == null) return null;
        
        NpcMonsterExcel npcMonster = GameData.getNpcMonsterExcelMap().get(rogueMonster.getNpcMonsterID());
        if (npcMonster == null) return null;
        
        // Actually create the monster now
        EntityMonster monster = new EntityMonster(scene, npcMonster, monsterInfo.getPos());
        monster.getRot().set(monsterInfo.getRot());
        monster.setGroupId(group.getId());
        monster.setInstId(monsterInfo.getID());
        monster.setEventId(rogueMonster.getEventID());
        monster.setOverrideStageId(rogueMonster.getEventID());
        
        return monster;
    }
}
