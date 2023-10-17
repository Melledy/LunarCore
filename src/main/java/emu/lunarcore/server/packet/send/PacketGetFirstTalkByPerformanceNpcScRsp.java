package emu.lunarcore.server.packet.send;

import emu.lunarcore.proto.GetFirstTalkByPerformanceNpcCsReqOuterClass.GetFirstTalkByPerformanceNpcCsReq;
import emu.lunarcore.proto.GetFirstTalkByPerformanceNpcScRspOuterClass.GetFirstTalkByPerformanceNpcScRsp;
import emu.lunarcore.proto.NpcTalkInfoOuterClass.NpcTalkInfo;
import emu.lunarcore.server.packet.BasePacket;
import emu.lunarcore.server.packet.CmdId;

public class PacketGetFirstTalkByPerformanceNpcScRsp extends BasePacket {

    public PacketGetFirstTalkByPerformanceNpcScRsp(GetFirstTalkByPerformanceNpcCsReq req) {
        super(CmdId.GetFirstTalkByPerformanceNpcScRsp);
        
        var data = GetFirstTalkByPerformanceNpcScRsp.newInstance();
        
        for (int id: req.getNpcTalkList()) {
            var info = NpcTalkInfo.newInstance()
                    .setNpcTalkId(id);
            
            data.addNpcTalkInfoList(info);
        }
        
        this.setData(data);
    }
}
