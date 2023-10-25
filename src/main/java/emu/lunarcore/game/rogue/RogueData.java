package emu.lunarcore.game.rogue;

import java.util.HashSet;
import java.util.Set;
import java.util.TreeMap;

import emu.lunarcore.data.excel.RogueAreaExcel;
import emu.lunarcore.game.player.Player;
import emu.lunarcore.proto.RogueCurrentInfoOuterClass.RogueCurrentInfo;
import emu.lunarcore.proto.RogueMapInfoOuterClass.RogueMapInfo;
import emu.lunarcore.proto.RogueRoomStatusOuterClass.RogueRoomStatus;
import emu.lunarcore.proto.RogueStatusOuterClass.RogueStatus;
import lombok.Getter;

@Getter
public class RogueData {
    private transient Player player;
    private transient RogueAreaExcel excel;
    
    private int currentRoomProgress;
    private int currentSiteId;
    private Set<Integer> baseAvatarIds;
    private TreeMap<Integer, RogueRoomData> rooms;
    
    public RogueData(Player player, RogueAreaExcel excel) {
        this.player = player;
        this.excel = excel;
        this.currentRoomProgress = 1;
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
                this.setCurrentRoom(roomData);
            }
        }
    }
    
    private void setCurrentRoom(RogueRoomData roomData) {
        this.currentSiteId = roomData.getSiteId();
        roomData.setStatus(RogueRoomStatus.ROGUE_ROOM_STATUS_PLAY); // TODO reset when changing rooms
    }
    
    public RogueRoomData getCurrentRoom() {
        return this.rooms.get(this.getCurrentSiteId());
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