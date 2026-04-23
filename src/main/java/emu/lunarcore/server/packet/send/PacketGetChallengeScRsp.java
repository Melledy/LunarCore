package emu.lunarcore.server.packet.send;

import emu.lunarcore.LunarCore;
import emu.lunarcore.data.GameData;
import emu.lunarcore.game.challenge.ChallengeType;
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
                // Get challenge history
                var history = player.getChallengeManager().getHistory().get(challengeExcel.getId());

                if (history != null) {
                    data.addChallengeList(history.toProto());
                } else {
                    // Create fake completed challenge proto
                    var proto = Challenge.newInstance()
                            .setChallengeId(challengeExcel.getId());

                    // Skip boss challenges for now TODO
                    if (challengeExcel.getType() == ChallengeType.BOSS) {
                        var boss = proto.getMutableExtInfo().getMutableBossInfo();
                        boss.getMutableFirstNode();
                        boss.getMutableSecondNode();
                    }

                    data.addChallengeList(proto);
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
