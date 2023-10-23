package emu.lunarcore.game.rogue;

import emu.lunarcore.data.GameData;
import emu.lunarcore.data.GameDepot;
import emu.lunarcore.data.excel.RogueMapExcel;
import emu.lunarcore.data.excel.RogueRoomExcel;
import emu.lunarcore.proto.RogueRoomOuterClass.RogueRoom;
import emu.lunarcore.proto.RogueRoomStatusOuterClass.RogueRoomStatus;
import emu.lunarcore.util.Utils;
import lombok.Getter;

@Getter
public class RogueRoomData {
    private int roomId;
    private int siteId;
    private int status;
    private int[] nextSiteIds;
    
    private transient RogueRoomExcel excel;
    
    public RogueRoomData(RogueMapExcel mapExcel) {
        this.siteId = mapExcel.getSiteID();
        this.nextSiteIds = mapExcel.getNextSiteIDList();
        
        int[] rooms = GameDepot.getRogueMapGen().get(this.siteId);
        if (rooms != null) {
            this.roomId = Utils.randomElement(rooms);
        }
    }
    
    public void setStatus(RogueRoomStatus status) {
        this.status = status.getNumber();
    }
    
    public RogueRoomExcel getRoomExcel() {
        if (excel == null) {
            excel = GameData.getRogueRoomExcelMap().get(this.getRoomId());
        }
        return excel;
    }
    
    public RogueRoom toProto() {
        var proto = RogueRoom.newInstance()
                .setRoomId(this.getRoomId())
                .setSiteId(this.getSiteId())
                .setRoomStatusValue(this.getStatus());
        
        return proto;
    }
}
