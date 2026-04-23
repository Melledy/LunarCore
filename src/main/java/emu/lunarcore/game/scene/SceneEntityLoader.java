package emu.lunarcore.game.scene;

import emu.lunarcore.data.GameData;
import emu.lunarcore.data.config.*;
import emu.lunarcore.game.enums.PlaneType;
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
            boolean isToLoad = true;

            if (group.getCategory() == GroupInfo.GroupCategory.Mission) {
                isToLoad = false;
            }
            
            // These cause the map to get stuck loading
            if (group.getGroupName().contains("Bug") || group.getGroupName().contains("Book") || group.getGroupName().contains("AngryBlock") || group.getGroupName().contains("ShelfDoor")) {
                isToLoad = false;
            }
            
            // Bad groups
            if (scene.getPlaneId() == 20501 && group.getId() >= 360) {
                isToLoad = false;
            }
            
            // Load groups
            if (group.getLoadSide() == GroupLoadSide.Server && isToLoad) {
                scene.loadGroup(group);
            }
        }
    }
    
    public EntityMonster loadMonster(Scene scene, GroupInfo group, MonsterInfo monsterInfo) {
        // Don't spawn entity if they have certain flags in their info
        if (monsterInfo.isIsDelete() || monsterInfo.isIsClientOnly() || !monsterInfo.isLoadOnInitial()) {
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
        if (propInfo.isIsDelete() /* || propInfo.isIsClientOnly() */ || !propInfo.isLoadOnInitial()) {
            return null;
        }
        
        // Get prop excel to make sure prop exists
        PropExcel propExcel = GameData.getPropExcelMap().get(propInfo.getPropID());
        if (propExcel == null) return null;
        
        // Remove these annoying props from the map
        if (propExcel.isDisabled()) {
            return null;
        }
        
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
        } else if (prop.getPropId() == 1025)  {
            // Show divergent universe prop
            prop.setState(PropState.Open, false);
        } else if (prop.getPropId() == 104029) { // Era flippers
            prop.setState(PropState.Open, false);
        } else if (prop.getPropId() == 104022) { // Mirage orb
            prop.setState(PropState.Closed, false);
        } else if (prop.getExcel().isDoor()) {
            // Hacky fix to always open doors
            prop.setState(PropState.Open, false);
        } else if (prop.getExcel().getPropType() == PropType.PROP_SPRING) {
            // Cache teleport anchors
            scene.getHealingSprings().add(prop);
        }

        if (scene.getPlaneType() != PlaneType.Raid && propExcel.getPropType() == PropType.PROP_ELEVATOR) {
            prop.setState(PropState.Elevator1);
        }
        
        // Add trigger to scene
        if (propInfo.getTrigger() != null) {
            scene.getTriggers().add(propInfo.getTrigger());
        }
        
        return prop;
    }
    
    public EntityNpc loadNpc(Scene scene, GroupInfo group, NpcInfo npcInfo) {
        // Don't spawn entity if they have certain flags in their info
        if (npcInfo.isIsDelete() || npcInfo.isIsClientOnly() || !npcInfo.isLoadOnInitial()) {
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