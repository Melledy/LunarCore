package emu.lunarcore.server.packet.send;

import emu.lunarcore.proto.GetRogueScoreRewardInfoScRspOuterClass.GetRogueScoreRewardInfoScRsp;
import emu.lunarcore.server.packet.BasePacket;
import emu.lunarcore.server.packet.CmdId;

public class PacketGetRogueScoreRewardInfoScRsp extends BasePacket {

    public PacketGetRogueScoreRewardInfoScRsp() {
        super(CmdId.GetRogueScoreRewardInfoScRsp);
        
        var data = GetRogueScoreRewardInfoScRsp.newInstance();
        
        data.getMutableScoreRewardInfo()
            .setPoolId(1) // TODO un hardcode
            .setPoolRefreshed(true)
            .setHasTakenInitialScore(true);
        
        this.setData(data);
    }
}
