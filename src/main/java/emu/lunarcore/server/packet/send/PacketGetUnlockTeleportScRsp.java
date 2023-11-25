package emu.lunarcore.server.packet.send;

import emu.lunarcore.data.GameData;
import emu.lunarcore.data.config.FloorInfo;
import emu.lunarcore.data.excel.MapEntranceExcel;
import emu.lunarcore.proto.GetUnlockTeleportScRspOuterClass.GetUnlockTeleportScRsp;
import emu.lunarcore.server.packet.BasePacket;
import emu.lunarcore.server.packet.CmdId;
import us.hebi.quickbuf.RepeatedInt;

public class PacketGetUnlockTeleportScRsp extends BasePacket {

    public PacketGetUnlockTeleportScRsp(RepeatedInt list) {
        super(CmdId.GetUnlockTeleportScRsp);
        
        var data = GetUnlockTeleportScRsp.newInstance();
        
        for (int entryId : list) {
            MapEntranceExcel excel = GameData.getMapEntranceExcelMap().get(entryId);
            if (excel == null) continue;
            
            FloorInfo floorInfo = GameData.getFloorInfo(excel.getPlaneID(), excel.getFloorID());
            if (floorInfo == null) continue;
            
            // Add unlocked teleport ids
            for (var teleport : floorInfo.getCachedTeleports().values()) {
                data.addAllUnlockedTeleportList(teleport.getMappingInfoID());
            }
        }
        
        this.setData(data);
    }
}
