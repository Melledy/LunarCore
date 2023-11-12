package emu.lunarcore.game.rogue;

import java.util.*;

import emu.lunarcore.data.GameData;
import emu.lunarcore.data.config.AnchorInfo;
import emu.lunarcore.data.excel.RogueAeonExcel;
import emu.lunarcore.data.excel.RogueAreaExcel;
import emu.lunarcore.data.excel.RogueMapExcel;
import emu.lunarcore.game.battle.Battle;
import emu.lunarcore.game.player.Player;
import emu.lunarcore.proto.BattleEndStatusOuterClass.BattleEndStatus;
import emu.lunarcore.proto.BattleStatisticsOuterClass.BattleStatistics;
import emu.lunarcore.proto.RogueAvatarInfoOuterClass.RogueAvatarInfo;
import emu.lunarcore.proto.RogueBuffInfoOuterClass.RogueBuffInfo;
import emu.lunarcore.proto.RogueBuffSourceOuterClass.RogueBuffSource;
import emu.lunarcore.proto.RogueCurrentInfoOuterClass.RogueCurrentInfo;
import emu.lunarcore.proto.RogueMapInfoOuterClass.RogueMapInfo;
import emu.lunarcore.proto.RogueMiracleInfoOuterClass.RogueMiracleInfo;
import emu.lunarcore.proto.RogueMiracleSourceOuterClass.RogueMiracleSource;
import emu.lunarcore.proto.RogueRoomStatusOuterClass.RogueRoomStatus;
import emu.lunarcore.proto.RogueStatusOuterClass.RogueStatus;
import emu.lunarcore.server.packet.send.*;
import emu.lunarcore.util.Utils;
import lombok.Getter;

@Getter
public class RogueInstance {
    private transient Player player;
    private transient RogueAreaExcel excel;
    
    private int areaId;
    private int currentRoomProgress;
    private int currentSiteId;
    private int startSiteId;
    private TreeMap<Integer, RogueRoomData> rooms;
    
    private Set<Integer> baseAvatarIds;
    private Map<Integer, RogueBuffData> buffs;
    private Map<Integer, RogueMiracleData> miracles;
    
    private int pendingBuffSelects;
    private RogueBuffSelectMenu buffSelect;
    private int pendingMiracleSelects;
    private RogueMiracleSelectMenu miracleSelect;
    
    private int aeonId;
    private int aeonBuffType;
    
    @Deprecated // Morphia only!
    public RogueInstance() {}
    
    public RogueInstance(Player player, RogueAreaExcel excel, RogueAeonExcel aeonExcel) {
        this.player = player;
        this.excel = excel;
        this.areaId = excel.getRogueAreaID();
        this.currentRoomProgress = 0;
        this.baseAvatarIds = new HashSet<>();
        this.buffs = new HashMap<>();
        this.miracles = new HashMap<>();
        
        if (aeonExcel != null) {
            this.aeonId = aeonExcel.getAeonID();
            this.aeonBuffType = aeonExcel.getRogueBuffType(); 
        }
        
        this.initRooms();
    }
    
    public RogueStatus getStatus() {
        return RogueStatus.ROGUE_STATUS_DOING;
    }
    
    private void initRooms() {
        if (this.rooms != null) return;
        
        this.rooms = new TreeMap<>();
        
        for (var mapExcel : this.getExcel().getSites()) {
            var roomData = new RogueRoomData(mapExcel);
            this.rooms.put(roomData.getSiteId(), roomData);
            
            if (mapExcel.isIsStart()) {
                this.startSiteId = roomData.getSiteId();
            }
        }
    }
    
    private RogueRoomData getRoomBySiteId(int siteId) {
        return this.rooms.get(siteId);
    }
    
    public RogueRoomData getCurrentRoom() {
        return this.getRoomBySiteId(this.getCurrentSiteId());
    }
    
    public synchronized void createBuffSelect(int amount) {
        this.pendingBuffSelects += amount;
        
        RogueBuffSelectMenu buffSelect = this.updateBuffSelect();
        if (buffSelect != null) {
            getPlayer().sendPacket(new PacketSyncRogueBuffSelectInfoScNotify(buffSelect));
        }
    }
    
    public synchronized RogueBuffSelectMenu updateBuffSelect() {
        if (this.pendingBuffSelects > 0 && this.getBuffSelect() == null) {
            this.buffSelect = new RogueBuffSelectMenu(this);
            this.pendingBuffSelects--;
            return this.buffSelect;
        }
        
        return null;
    }
    
    public synchronized RogueBuffSelectMenu rollBuffSelect() {
        if (getBuffSelect() != null && getBuffSelect().hasRerolls()) {
            this.getBuffSelect().reroll();
            return this.getBuffSelect();
        }
        
        return null;
    }
    
    public synchronized RogueBuffData selectBuff(int buffId) {
        if (this.getBuffSelect() == null) return null;
        
        RogueBuffData buff = this.getBuffSelect().getBuffs()
                .stream()
                .filter(b -> b.getId() == buffId)
                .findFirst()
                .orElse(null);
        
        if (buff == null) return null;
        
        this.buffSelect = null;
        this.getBuffs().put(buff.getId(), buff);
        getPlayer().sendPacket(new PacketAddRogueBuffScNotify(buff, RogueBuffSource.ROGUE_BUFF_SOURCE_TYPE_SELECT));
        
        return buff;
    }
    
    public synchronized void createMiracleSelect(int amount) {
        this.pendingMiracleSelects += amount;
        
        RogueMiracleSelectMenu miracleSelect = this.updateMiracleSelect();
        if (miracleSelect != null) {
            getPlayer().sendPacket(new PacketSyncRogueMiracleSelectInfoScNotify(miracleSelect));
        }
    }
    
    public synchronized RogueMiracleSelectMenu updateMiracleSelect() {
        if (this.pendingMiracleSelects > 0 && this.getMiracleSelect() == null) {
            this.miracleSelect = new RogueMiracleSelectMenu(this);
            this.pendingMiracleSelects--;
            return this.miracleSelect;
        }
        
        return null;
    }
    
    public synchronized RogueMiracleData selectMiracle(int miracleId) {
        if (this.getMiracleSelect() == null) return null;
        
        RogueMiracleData miracle = this.getMiracleSelect().getMiracles()
                .stream()
                .filter(b -> b.getId() == miracleId)
                .findFirst()
                .orElse(null);
        
        if (miracle == null) return null;
        
        this.miracleSelect = null;
        this.getMiracles().put(miracle.getId(), miracle);
        getPlayer().sendPacket(new PacketAddRogueMiracleScNotify(miracle, RogueMiracleSource.ROGUE_MIRACLE_SOURCE_TYPE_SELECT));
        
        return miracle;
    }
    
    public synchronized RogueRoomData enterRoom(int siteId) {
        // Set status on previous room
        RogueRoomData prevRoom = this.getCurrentRoom();
        if (prevRoom != null) {
            // Make sure the site we want to go into is connected to the current room we are in
            if (!Utils.arrayContains(prevRoom.getNextSiteIds(), siteId)) {
                return null;
            }
            // Update status
            prevRoom.setStatus(RogueRoomStatus.ROGUE_ROOM_STATUS_FINISH);
        }
        
        // Get next room
        RogueRoomData nextRoom = this.getRoomBySiteId(siteId);
        if (nextRoom == null) return null;
        
        // Enter room
        this.currentRoomProgress++;
        this.currentSiteId = nextRoom.getSiteId();
        nextRoom.setStatus(RogueRoomStatus.ROGUE_ROOM_STATUS_PLAY);
        
        // Enter scene
        boolean success = getPlayer().enterScene(nextRoom.getRoomExcel().getMapEntrance(), 0, false);
        if (!success) return null;
        
        // Move player to rogue start position
        AnchorInfo anchor = getPlayer().getScene().getFloorInfo().getAnchorInfo(nextRoom.getExcel().getGroupID(), 1);
        if (anchor != null) {
            getPlayer().getPos().set(anchor.getPos());
            getPlayer().getRot().set(anchor.getRot());
        }
        
        // Send packet if we are not entering the rogue instance for the first time
        if (prevRoom != null) {
            getPlayer().sendPacket(new PacketSyncRogueMapRoomScNotify(this, prevRoom));
            getPlayer().sendPacket(new PacketSyncRogueMapRoomScNotify(this, nextRoom));
        }
        
        return nextRoom;
    }
    
    // Dialogue stuff
    
    public void selectDialogue(int dialogueEventId) {
    
    }
    
    // Battle
    
    public synchronized void onBattleStart(Battle battle) {
        // Add rogue blessings as battle buffs
        for (var buff : this.getBuffs().values()) {
            battle.addBuff(buff.toMazeBuff());
        }
        // Set monster level for battle
        RogueMapExcel mapExcel = GameData.getRogueMapExcel(this.getExcel().getMapId(), this.getCurrentSiteId());
        if (mapExcel != null && mapExcel.getLevelList() != null && mapExcel.getLevelList().length >= 1) {
            battle.setLevelOverride(mapExcel.getLevelList()[0]);
        }
    }
    
    public synchronized void onBattleFinish(Battle battle, BattleEndStatus result, BattleStatistics stats) {
        if (result == BattleEndStatus.BATTLE_END_WIN) {
            int amount = battle.getNpcMonsters().size();
            this.createBuffSelect(amount);
        }
    }
    
    // Database
    
    public void onLoad(Player player) {
        this.player = player;
        this.excel = GameData.getRogueAreaExcelMap().get(areaId);
        
        if (this.getBuffSelect() != null) {
            this.getBuffSelect().onLoad(this);
        }
        if (this.getMiracleSelect() != null) {
            this.getMiracleSelect().onLoad(this);
        }
    }
    
    // Serialization

    public RogueCurrentInfo toProto() {
        var proto = RogueCurrentInfo.newInstance()
                .setStatus(this.getStatus())
                .setRogueAvatarInfo(this.toAvatarInfoProto())
                .setRoomMap(this.toMapInfoProto())
                .setRogueBuffInfo(this.toBuffInfoProto())
                .setRogueMiracleInfo(this.toMiracleInfoProto());
        
        return proto;
    }
    
    public RogueAvatarInfo toAvatarInfoProto() {
        var proto = RogueAvatarInfo.newInstance();
        
        for (int id : this.getBaseAvatarIds()) {
            proto.addBaseAvatarIdList(id);
        }
        
        return proto;
    }
    
    public RogueMapInfo toMapInfoProto() {
        var room = this.getCurrentRoom();

        var proto = RogueMapInfo.newInstance()
                .setAreaId(this.getExcel().getId())
                .setMapId(this.getExcel().getMapId())
                .setCurSiteId(room.getSiteId())
                .setCurRoomId(room.getRoomId());
        
        for (var roomData : this.getRooms().values()) {
            proto.addRoomList(roomData.toProto());
        }
        
        return proto;
    }
    
    public RogueBuffInfo toBuffInfoProto() {
        var proto = RogueBuffInfo.newInstance();
        
        if (this.getBuffSelect() != null) {
            proto.setBuffSelectInfo(this.getBuffSelect().toProto());
        } else {
            proto.getMutableBuffSelectInfo();
        }
        
        for (var buff : this.getBuffs().values()) {
            proto.addMazeBuffList(buff.toProto());
        }
        
        return proto;
    }
    
    public RogueMiracleInfo toMiracleInfoProto() {
        var proto = RogueMiracleInfo.newInstance();
        
        if (this.getMiracleSelect() != null) {
            proto.setMiracleSelectInfo(this.getMiracleSelect().toProto());
        } else {
            proto.getMutableMiracleSelectInfo();
        }
        
        // Set flag for this so it gets serialized
        proto.getMutableAchivedMiracleInfo();
        
        for (var miracle : this.getMiracles().values()) {
            proto.getMutableAchivedMiracleInfo().addRogueMiracleList(miracle.toProto());
        }
        
        return proto;
    }
    
}