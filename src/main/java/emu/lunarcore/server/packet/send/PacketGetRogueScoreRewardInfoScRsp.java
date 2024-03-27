package emu.lunarcore.server.packet.send;

import java.util.stream.IntStream;
import emu.lunarcore.game.player.Player;
import emu.lunarcore.proto.GetRogueScoreRewardInfoScRspOuterClass.GetRogueScoreRewardInfoScRsp;
import emu.lunarcore.server.packet.BasePacket;
import emu.lunarcore.server.packet.CmdId;

import java.util.concurrent.TimeUnit;

public class PacketGetRogueScoreRewardInfoScRsp extends BasePacket {

    public PacketGetRogueScoreRewardInfoScRsp(Player player) {
        super(CmdId.GetRogueScoreRewardInfoScRsp);
        
        var data = GetRogueScoreRewardInfoScRsp.newInstance();

        long beginTime = (System.currentTimeMillis() / 1000) - TimeUnit.DAYS.toSeconds(1);
        long endTime = beginTime + TimeUnit.DAYS.toSeconds(8);

        data.getMutableScoreRewardInfo()
            .setPoolId(20 + player.getWorldLevel()) // TODO pool ids should not change when world level changes
            .setPoolRefreshed(false)
            .setScore(14000)
            .setHasTakenInitialScore(true)
            .setBeginTime(beginTime)
            .setEndTime(endTime)
            .addAllHasTakenReward(IntStream.rangeClosed(1, 20).flatMap(i -> IntStream.generate(() -> i).limit(20)).toArray());
        
        this.setData(data);
    }
}
