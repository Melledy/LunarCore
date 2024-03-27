package emu.lunarcore.server.packet.send;

import java.util.stream.IntStream;
import emu.lunarcore.proto.GetRogueInitialScoreScRspOuterClass.GetRogueInitialScoreScRsp;
import emu.lunarcore.proto.RogueScoreRewardInfoOuterClass.RogueScoreRewardInfo;
import emu.lunarcore.server.packet.BasePacket;
import emu.lunarcore.server.packet.CmdId;

public class PacketGetRogueInitialScoreScRsp extends BasePacket {

    public PacketGetRogueInitialScoreScRsp() {
        super(CmdId.GetRogueInitialScoreScRsp);
        
        var score = RogueScoreRewardInfo.newInstance()
            .setPoolId(26) // TODO pool ids should not change when world level changes
            .setPoolRefreshed(true)
            .setHasTakenInitialScore(true)
            .setScore(14000)
            .setBeginTime(0)
            .setEndTime(1999999999)
            .addAllHasTakenReward(IntStream.rangeClosed(1, 20).flatMap(i -> IntStream.generate(() -> i).limit(20)).toArray());
        
        var proto = GetRogueInitialScoreScRsp.newInstance()
            .setRogueScoreInfo(score);

        this.setData(proto);
    }
}
