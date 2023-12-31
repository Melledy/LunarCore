package emu.lunarcore.game.scene;

import emu.lunarcore.data.GameData;
import emu.lunarcore.data.config.GroupInfo;
import emu.lunarcore.data.config.MonsterInfo;
import emu.lunarcore.data.config.NpcInfo;
import emu.lunarcore.data.config.PropInfo;
import emu.lunarcore.data.config.GroupInfo.GroupLoadSide;
import emu.lunarcore.data.excel.NpcMonsterExcel;
import emu.lunarcore.data.excel.PropExcel;
import emu.lunarcore.game.enums.PropState;
import emu.lunarcore.game.enums.PropType;
import emu.lunarcore.game.scene.entity.EntityMonster;
import emu.lunarcore.game.scene.entity.EntityNpc;
import emu.lunarcore.game.scene.entity.EntityProp;
import emu.lunarcore.game.scene.entity.GameEntity;

public class SceneEntityLoader {
    
    public void onSceneLoad(Scene scene) {
        for (GroupInfo group : scene.getFloorInfo().getGroups().values()) {
            // Skip non-server groups
            if (group.getLoadSide() != GroupLoadSide.Server) {
                continue;
            }
            
            // Load group
            scene.loadGroup(group);
        }
    }
    
    public EntityMonster loadMonster(Scene scene, GroupInfo group, MonsterInfo monsterInfo) {
        // Don't spawn entity if they have certain flags in their info
        if (monsterInfo.isIsDelete() || monsterInfo.isIsClientOnly()) {
            return null;
        }
        
        // Get excels from game data
        NpcMonsterExcel npcMonsterExcel = GameData.getNpcMonsterExcelMap().get(monsterInfo.getNPCMonsterID());
        if (npcMonsterExcel == null) return null;
        
        // Create monster from group monster info
        EntityMonster monster = new EntityMonster(scene, npcMonsterExcel, group, monsterInfo);
        monster.setEventId(monsterInfo.getEventID());
        monster.setWorldLevel(scene.getPlayer().getWorldLevel());
        
        return monster;
    }
    
    public EntityProp loadProp(Scene scene, GroupInfo group, PropInfo propInfo) {
        // Don't spawn entity if they have certain flags in their info
        if (propInfo.isIsDelete() || propInfo.isIsClientOnly()) {
            return null;
        }
        
        // Get prop excel to make sure prop exists
        PropExcel propExcel = GameData.getPropExcelMap().get(propInfo.getPropID());
        if (propExcel == null) return null;
        
        // Create prop from group prop info
        EntityProp prop = new EntityProp(scene, propExcel, group, propInfo);
        prop.setState(propInfo.getState(), false);
        
        // Cache
        if (prop.getPropId() == 1003) {
            // Hacky fix to open simulated universe
            if (propInfo.getMappingInfoID() == 2220) {
                // Regular simulated universe is locked behind a mission requirement by default
                prop.setState(PropState.Open, false);
            } else {
                // Skip tutorial simulated universe
                return null;
            }
        } else if (prop.getExcel().isDoor()) {
            // Hacky fix to always open doors
            prop.setState(PropState.Open, false);
        } else if (prop.getExcel().getPropType() == PropType.PROP_SPRING) {
            // Cache teleport anchors
            scene.getHealingSprings().add(prop);
        }
        
        // Add trigger to scene
        if (propInfo.getTrigger() != null) {
            scene.getTriggers().add(propInfo.getTrigger());
        }
        
        return prop;
    }
    
    public EntityNpc loadNpc(Scene scene, GroupInfo group, NpcInfo npcInfo) {
        // Don't spawn entity if they have certain flags in their info
        if (npcInfo.isIsDelete() || npcInfo.isIsClientOnly()) {
            return null;
        }
        
        // Sanity check npc id
        if (!GameData.getNpcExcelMap().containsKey(npcInfo.getNPCID())) {
            return null;
        }
        
        // Dont spawn duplicate NPCs
        boolean hasDuplicateNpcId = false;
        for (GameEntity entity : scene.getEntities().values()) {
            if (entity instanceof EntityNpc eNpc && eNpc.getNpcId() == npcInfo.getNPCID()) {
                hasDuplicateNpcId = true;
                break;
            }
        }
        if (hasDuplicateNpcId) return null;
        
        // Create npc from group and npc info
        return new EntityNpc(scene, group, npcInfo);
    }
}
