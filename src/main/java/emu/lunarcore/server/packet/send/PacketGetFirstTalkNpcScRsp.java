package emu.lunarcore.server.packet.send;

import emu.lunarcore.proto.FirstNpcTalkInfoOuterClass.FirstNpcTalkInfo;
import emu.lunarcore.proto.GetFirstTalkNpcCsReqOuterClass.GetFirstTalkNpcCsReq;
import emu.lunarcore.proto.GetFirstTalkNpcScRspOuterClass.GetFirstTalkNpcScRsp;
import emu.lunarcore.server.packet.BasePacket;
import emu.lunarcore.server.packet.CmdId;

public class PacketGetFirstTalkNpcScRsp extends BasePacket {

    public PacketGetFirstTalkNpcScRsp(GetFirstTalkNpcCsReq req) {
        super(CmdId.GetFirstTalkNpcScRsp);

        var data = GetFirstTalkNpcScRsp.newInstance();

        for (int id: req.getNpcIdList()) {
            var info = FirstNpcTalkInfo.newInstance()
                    .setNpcId(id);

            data.addNpcTalkInfoList(info);
        }

        this.setData(data);
    }
}
