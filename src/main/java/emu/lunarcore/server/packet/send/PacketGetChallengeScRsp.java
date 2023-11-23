package emu.lunarcore.server.packet.send;

import emu.lunarcore.LunarCore;
import emu.lunarcore.data.GameData;
import emu.lunarcore.game.player.Player;
import emu.lunarcore.proto.ChallengeOuterClass.Challenge;
import emu.lunarcore.proto.GetChallengeScRspOuterClass.GetChallengeScRsp;
import emu.lunarcore.server.packet.BasePacket;
import emu.lunarcore.server.packet.CmdId;

public class PacketGetChallengeScRsp extends BasePacket {

    public PacketGetChallengeScRsp(Player player) {
        super(CmdId.GetChallengeScRsp);

        var data = GetChallengeScRsp.newInstance();
        
        if (LunarCore.getConfig().getServerOptions().unlockAllChallenges) {
            // Add all challenge excels to our challenge list
            // TODO find out which challenge groups are active so we dont have to send old challenge ids to the client
            for (var challengeExcel : GameData.getChallengeExcelMap().values()) {
                var history = player.getChallengeManager().getHistory().get(challengeExcel.getId());
                
                if (history != null) {
                    data.addChallengeList(history.toProto());
                } else {
                    data.addChallengeList(Challenge.newInstance().setChallengeId(challengeExcel.getId()));
                }
            }
        } else {
            for (var history : player.getChallengeManager().getHistory().values()) {
                data.addChallengeList(history.toProto());
            }
        }
        
        for (var reward : player.getChallengeManager().getTakenRewards().values()) {
            data.addChallengeRewardList(reward.toProto());
        }
        
        this.setData(data);
    }
}
