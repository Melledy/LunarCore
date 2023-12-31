package emu.lunarcore.server.packet.send;

import emu.lunarcore.proto.FarmStageGachaInfoOuterClass.FarmStageGachaInfo;
import emu.lunarcore.proto.GetFarmStageGachaInfoScRspOuterClass.GetFarmStageGachaInfoScRsp;
import emu.lunarcore.server.packet.BasePacket;
import emu.lunarcore.server.packet.CmdId;
import us.hebi.quickbuf.RepeatedInt;

public class PacketGetFarmStageGachaInfoScRsp extends BasePacket {

    public PacketGetFarmStageGachaInfoScRsp(RepeatedInt idList) {
        super(CmdId.GetFarmStageGachaInfoScRsp);
        
        var data = GetFarmStageGachaInfoScRsp.newInstance();
        
        for (var id : idList) {
            var info = FarmStageGachaInfo.newInstance()
                    .setGachaId(id)
                    .setEndTime(Integer.MAX_VALUE);
            
            data.addFarmStageGachaInfoList(info);
        }
        
        this.setData(data);
    }
}
