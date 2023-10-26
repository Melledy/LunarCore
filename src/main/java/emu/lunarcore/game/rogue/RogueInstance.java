package emu.lunarcore.game.rogue;

import java.util.HashSet;
import java.util.Set;
import java.util.TreeMap;

import emu.lunarcore.data.config.AnchorInfo;
import emu.lunarcore.data.excel.RogueAreaExcel;
import emu.lunarcore.game.player.Player;
import emu.lunarcore.proto.RogueCurrentInfoOuterClass.RogueCurrentInfo;
import emu.lunarcore.proto.RogueMapInfoOuterClass.RogueMapInfo;
import emu.lunarcore.proto.RogueRoomStatusOuterClass.RogueRoomStatus;
import emu.lunarcore.proto.RogueStatusOuterClass.RogueStatus;
import emu.lunarcore.server.packet.send.PacketSyncRogueMapRoomScNotify;
import emu.lunarcore.util.Utils;
import lombok.Getter;

@Getter
public class RogueInstance {
    private transient Player player;
    private transient RogueAreaExcel excel;
    
    private int currentRoomProgress;
    private int currentSiteId;
    private int startSiteId;
    private Set<Integer> baseAvatarIds;
    private TreeMap<Integer, RogueRoomData> rooms;
    
    public RogueInstance(Player player, RogueAreaExcel excel) {
        this.player = player;
        this.excel = excel;
        this.currentRoomProgress = 0;
        this.baseAvatarIds = new HashSet<>();
        
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
    
    // Serialization

    public RogueCurrentInfo toProto() {
        var proto = RogueCurrentInfo.newInstance()
                .setStatus(this.getStatus())
                .setRoomMap(this.toMapProto());
        
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
    
}