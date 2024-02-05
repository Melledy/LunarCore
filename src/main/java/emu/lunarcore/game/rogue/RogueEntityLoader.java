package emu.lunarcore.game.rogue;

import emu.lunarcore.data.GameData;
import emu.lunarcore.data.GameDepot;
import emu.lunarcore.data.config.GroupInfo;
import emu.lunarcore.data.config.MonsterInfo;
import emu.lunarcore.data.config.NpcInfo;
import emu.lunarcore.data.config.PropInfo;
import emu.lunarcore.data.excel.NpcMonsterExcel;
import emu.lunarcore.data.excel.PropExcel;
import emu.lunarcore.data.excel.RogueMonsterExcel;
import emu.lunarcore.data.excel.RogueNPCExcel;
import emu.lunarcore.game.enums.PropState;
import emu.lunarcore.game.scene.Scene;
import emu.lunarcore.game.scene.SceneEntityLoader;
import emu.lunarcore.game.scene.entity.EntityMonster;
import emu.lunarcore.game.scene.entity.EntityNpc;
import emu.lunarcore.game.scene.entity.EntityProp;
import emu.lunarcore.game.scene.entity.extra.PropRogueData;
import emu.lunarcore.server.packet.send.PacketSyncRogueDialogueEventDataScNotify;
import emu.lunarcore.util.Utils;

public class RogueEntityLoader extends SceneEntityLoader {
    
    @Override
    public void onSceneLoad(Scene scene) {
        // Make sure player is in a rogue instance
        RogueInstance rogue = scene.getPlayer().getRogueInstance();
        if (rogue == null) return;
        
        // Get current room
        RogueRoomData room = rogue.getCurrentRoom();
        if (room == null) return;
        
        // Load scene groups
        for (int key : room.getExcel().getGroupWithContent().keySet()) {
            scene.loadGroup(key);
        }
    }
    
    @Override
    public EntityMonster loadMonster(Scene scene, GroupInfo group, MonsterInfo monsterInfo) {
        // Make sure player is in a rogue instance
        RogueInstance rogue = scene.getPlayer().getRogueInstance();
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
        EntityMonster monster = new EntityMonster(scene, npcMonster, group, monsterInfo);
        monster.setEventId(rogueMonster.getEventID());
        monster.setCustomStageId(rogueMonster.getEventID());
        
        return monster;
    }
    
    @Override
    public EntityProp loadProp(Scene scene, GroupInfo group, PropInfo propInfo) {
        // Make sure player is in a rogue instance
        RogueInstance rogue = scene.getPlayer().getRogueInstance();
        if (rogue == null) return null;
        
        // Set variables here so we can override them later if we need
        int propId = propInfo.getPropID();
        PropState state = propInfo.getState();
        PropRogueData propExtra = null;
        
        // Rogue Door id is 1000
        if (propId == 1000 || propId == 1021 || propId == 1022 || propId == 1023) {
            // Site index
            int index = 0;
            
            // Eww
            if (propInfo.getName().equals("Door2")) {
                index = 1;
            }
            
            // Get portal data
            RogueRoomData room = rogue.getCurrentRoom();
            if (room.getNextSiteIds().length > 0) {
                int siteId = room.getNextSiteIds()[index];
                var nextRoom = rogue.getRooms().get(siteId);
                
                propId = switch (nextRoom.getRoomExcel().getRogueRoomType()) {
                    case 3,8 -> 1022;
                    case 5 -> 1023;
                    default -> 1021;
                };
                propExtra = new PropRogueData(nextRoom.getRoomId(), siteId);
            } else {
                // Exit portal?
                propId = 1000;
            }

            // Force rogue door to be open
            state = PropState.Open;
        }
        
        // Get prop excel
        PropExcel propExcel = GameData.getPropExcelMap().get(propId);
        if (propExcel == null) return null;
        
        // Create prop from prop info
        EntityProp prop = new EntityProp(scene, propExcel, group, propInfo);
        prop.setState(state, false);
        
        // Overrides
        if (propExtra != null) {
            prop.setRogueData(propExtra);
        }
        
        // Add trigger
        if (propInfo.getTrigger() != null) {
            scene.getTriggers().add(propInfo.getTrigger());
        }
        
        return prop;
    }
    
    @Override
    public EntityNpc loadNpc(Scene scene, GroupInfo group, NpcInfo npcInfo) {
        // Create npc from group npc info
        EntityNpc npc = super.loadNpc(scene, group, npcInfo);
        
        // Add rogue dialogue
        if (npc.getNpcId() == 3013) {
            int npcId;
            RogueInstance instance;
            do {
                RogueNPCExcel rogueNpcExcel = Utils.randomElement(GameDepot.getRogueRandomNpcList());
                npcId = rogueNpcExcel.getId();
                instance = scene.getPlayer().getRogueInstance();
            } while (instance.setDialogueParams(npcId) == null);
            
            instance.getEventManager().setNowPercentage(0);
            npc.setRogueNpcId(npcId);
            npc.setEventId(++instance.eventUniqueId);
            scene.getPlayer().sendPacket(new PacketSyncRogueDialogueEventDataScNotify(npcId, instance.curDialogueParams.get(npcId),
                instance.eventUniqueId));
        }
        
        return npc;
    }
}
