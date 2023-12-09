package emu.lunarcore.server.packet.send;

import emu.lunarcore.data.GameData;
import emu.lunarcore.proto.EnteredSceneInfoOuterClass.EnteredSceneInfo;
import emu.lunarcore.proto.GetEnteredSceneScRspOuterClass.GetEnteredSceneScRsp;
import emu.lunarcore.server.packet.BasePacket;
import emu.lunarcore.server.packet.CacheablePacket;
import emu.lunarcore.server.packet.CmdId;

@CacheablePacket
public class PacketGetEnteredSceneScRsp extends BasePacket {

    public PacketGetEnteredSceneScRsp() {
        super(CmdId.GetEnteredSceneScRsp);
        
        var data = GetEnteredSceneScRsp.newInstance();
        
        // TODO find a better way to get these scenes
        for (var excel : GameData.getMapEntranceExcelMap().values()) {
            // Skip these
            if (excel.getFinishMainMissionList().length == 0 && excel.getFinishSubMissionList().length == 0) {
                continue;
            }
            
            // Add info
            var info = EnteredSceneInfo.newInstance()
                    .setPlaneId(excel.getPlaneID())
                    .setFloorId(excel.getFloorID());
            
            data.addEnteredSceneInfo(info);
        }
        
        this.setData(data);
    }
}
