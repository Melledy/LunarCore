package emu.lunarcore.server.packet.send;

import emu.lunarcore.proto.GetChallengeGroupStatisticsScRspOuterClass.GetChallengeGroupStatisticsScRsp;
import emu.lunarcore.server.packet.BasePacket;
import emu.lunarcore.server.packet.CmdId;

public class PacketGetChallengeGroupStatisticsScRsp extends BasePacket {

    public PacketGetChallengeGroupStatisticsScRsp(int groupId) {
        super(CmdId.GetChallengeGroupStatisticsScRsp);

        var data = GetChallengeGroupStatisticsScRsp.newInstance()
                .setGroupId(groupId); // TODO: set the rest of the fields

        this.setData(data);
    }
}
