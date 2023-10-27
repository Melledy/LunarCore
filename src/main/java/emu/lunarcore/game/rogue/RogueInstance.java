package emu.lunarcore.game.rogue;

import java.util.*;

import emu.lunarcore.data.GameData;
import emu.lunarcore.data.config.AnchorInfo;
import emu.lunarcore.data.excel.RogueAreaExcel;
import emu.lunarcore.game.battle.Battle;
import emu.lunarcore.game.player.Player;
import emu.lunarcore.proto.BattleEndStatusOuterClass.BattleEndStatus;
import emu.lunarcore.proto.BattleStatisticsOuterClass.BattleStatistics;
import emu.lunarcore.proto.RogueBuffInfoOuterClass.RogueBuffInfo;
import emu.lunarcore.proto.RogueBuffSourceOuterClass.RogueBuffSource;
import emu.lunarcore.proto.RogueCurrentInfoOuterClass.RogueCurrentInfo;
import emu.lunarcore.proto.RogueMapInfoOuterClass.RogueMapInfo;
import emu.lunarcore.proto.RogueRoomStatusOuterClass.RogueRoomStatus;
import emu.lunarcore.proto.RogueStatusOuterClass.RogueStatus;
import emu.lunarcore.server.packet.send.PacketAddRogueBuffScNotify;
import emu.lunarcore.server.packet.send.PacketSyncRogueBuffSelectInfoScNotify;
import emu.lunarcore.server.packet.send.PacketSyncRogueMapRoomScNotify;
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
    
    private RogueBuffSelectMenu buffSelect;
    private int pendingBuffSelects;
    
    @Deprecated // Morphia only!
    public RogueInstance() {}
    
    public RogueInstance(Player player, RogueAreaExcel excel) {
        this.player = player;
        this.excel = excel;
        this.areaId = excel.getRogueAreaID();
        this.currentRoomProgress = 0;
        this.baseAvatarIds = new HashSet<>();
        this.buffs = new HashMap<>();
        
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
    
    public synchronized RogueBuffData selectBuff(int buffId) {
        if (this.getBuffSelect() == null) return null;
        
        RogueBuffData buff = this.getBuffSelect().getBuffs()
                .stream()
                .filter(b -> b.getBuffId() == buffId)
                .findFirst()
                .orElse(null);
        
        if (buff == null) return null;
        
        this.buffSelect = null;
        this.getBuffs().put(buff.getBuffId(), buff);
        getPlayer().sendPacket(new PacketAddRogueBuffScNotify(buff, RogueBuffSource.ROGUE_BUFF_SOURCE_TYPE_SELECT));
        
        return buff;
    }
    
    public synchronized RogueRoomData enterRoom(int siteId) {
        // Set status on previous room
        RogueRoomData prevRoom = getCurrentRoom();
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
        
        // Load scene groups. THIS NEEDS TO BE LAST
        for (int key : nextRoom.getExcel().getGroupWithContent().keySet()) {
            getPlayer().getScene().loadGroup(key);
        }
        
        // Send packet if we are not entering the rogue instance for the first time
        if (prevRoom != null) {
            getPlayer().sendPacket(new PacketSyncRogueMapRoomScNotify(this, prevRoom));
            getPlayer().sendPacket(new PacketSyncRogueMapRoomScNotify(this, nextRoom));
        }
        
        return nextRoom;
    }
    
    public synchronized void onBattleStart(Battle battle) {
        for (var buff : this.getBuffs().values()) {
            battle.addBuff(buff.toMazeBuff());
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
    }
    
    // Serialization

    public RogueCurrentInfo toProto() {
        var proto = RogueCurrentInfo.newInstance()
                .setStatus(this.getStatus())
                .setRoomMap(this.toMapProto())
                .setRogueBuffInfo(this.toBuffProto());
        
        return proto;
    }
    
    public RogueMapInfo toMapProto() {
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
    
    public RogueBuffInfo toBuffProto() {
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
    
}