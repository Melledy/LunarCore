package emu.lunarcore.server.packet.send;

import emu.lunarcore.proto.BigDataRecommendTypeOuterClass.BigDataRecommendType;
import emu.lunarcore.proto.GetBigDataAllRecommendScRspOuterClass.GetBigDataAllRecommendScRsp;
import emu.lunarcore.server.packet.BasePacket;
import emu.lunarcore.server.packet.CmdId;

public class PacketGetBigDataAllRecommendScRsp extends BasePacket {

    public PacketGetBigDataAllRecommendScRsp(BigDataRecommendType type) {
        super(CmdId.GetBigDataAllRecommendScRsp);

        var data = GetBigDataAllRecommendScRsp.newInstance()
                .setRecommendType(type);
        
        if (type == BigDataRecommendType.BIG_DATA_RECOMMEND_TYPE_AVATAR_RELIC) {
            data.getMutableRelicReccomendData();
        } else {
            data.getMutableAvatarReccomendData();
        }
        
        this.setData(data);
    }
}
