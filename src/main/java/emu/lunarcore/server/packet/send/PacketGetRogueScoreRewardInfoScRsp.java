package emu.lunarcore.server.packet.send;

import emu.lunarcore.game.player.Player;
import emu.lunarcore.proto.GetRogueScoreRewardInfoScRspOuterClass.GetRogueScoreRewardInfoScRsp;
import emu.lunarcore.server.packet.BasePacket;
import emu.lunarcore.server.packet.CmdId;

public class PacketGetRogueScoreRewardInfoScRsp extends BasePacket {

    public PacketGetRogueScoreRewardInfoScRsp(Player player) {
        super(CmdId.GetRogueScoreRewardInfoScRsp);
        
        var data = GetRogueScoreRewardInfoScRsp.newInstance();
        
        data.getMutableScoreRewardInfo()
            .setPoolId(20 + player.getWorldLevel()) // TODO pool ids should not change when world level changes
            .setPoolRefreshed(true)
            .setHasTakenInitialScore(true);
        
        this.setData(data);
    }
}
