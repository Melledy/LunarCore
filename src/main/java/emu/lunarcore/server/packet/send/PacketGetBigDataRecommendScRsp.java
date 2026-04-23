package emu.lunarcore.server.packet.send;

import emu.lunarcore.proto.GetBigDataRecommendCsReqOuterClass.GetBigDataRecommendCsReq;
import emu.lunarcore.proto.GetBigDataRecommendScRspOuterClass.GetBigDataRecommendScRsp;
import emu.lunarcore.server.packet.BasePacket;
import emu.lunarcore.server.packet.CmdId;

public class PacketGetBigDataRecommendScRsp extends BasePacket {

    public PacketGetBigDataRecommendScRsp(GetBigDataRecommendCsReq req) {
        super(CmdId.GetBigDataRecommendScRsp);

        var data = GetBigDataRecommendScRsp.newInstance()
                .setRecommendType(req.getRecommendType())
                .setTrailblazerPathId(req.getTrailblazerPathId());

        this.setData(data);
    }
}
