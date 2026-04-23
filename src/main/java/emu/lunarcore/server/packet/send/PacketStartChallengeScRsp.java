package emu.lunarcore.server.packet.send;

import emu.lunarcore.game.challenge.ChallengeInstance;
import emu.lunarcore.game.challenge.ChallengeType;
import emu.lunarcore.game.player.Player;
import emu.lunarcore.proto.ExtraLineupTypeOuterClass.ExtraLineupType;
import emu.lunarcore.proto.StartChallengeScRspOuterClass.StartChallengeScRsp;
import emu.lunarcore.server.packet.BasePacket;
import emu.lunarcore.server.packet.CmdId;
import emu.lunarcore.server.packet.Retcode;
import us.hebi.quickbuf.RepeatedInt;

public class PacketStartChallengeScRsp extends BasePacket {

    public PacketStartChallengeScRsp(Retcode retcode) {
        super(CmdId.StartChallengeScRsp);

        var data = StartChallengeScRsp.newInstance()
                .setRetcode(retcode.getVal());

        this.setData(data);
    }

    public PacketStartChallengeScRsp(Player player, int challengeId, RepeatedInt lineup1, RepeatedInt lineup2) {
        super(CmdId.StartChallengeScRsp);

        var data = StartChallengeScRsp.newInstance();
        var challenge = player.getInstance(ChallengeInstance.class);

        if (challenge != null) {
            data.setScene(player.getScene().toProto());
            data.setChallengeInfo(challenge.toProto());

            // Add challenge lineups
            data.addLineupList(player.getLineupManager().getExtraLineupByType(ExtraLineupType.LINEUP_CHALLENGE_VALUE).toProto());

            if (challenge.getExcel().getStageNum() >= 2) {
                data.addLineupList(player.getLineupManager().getExtraLineupByType(ExtraLineupType.LINEUP_CHALLENGE_2_VALUE).toProto());
            }

            // Fix for challenge boss instances
            if (challenge.getType() == ChallengeType.BOSS) {
                var info = data.getMutableExtInfo().getMutableBossInfo();

                info.getMutableFirstNode();
                info.getMutableSecondNode();
            } else if (challenge.getType() == ChallengeType.STORY) {
                data.getMutableExtInfo().getMutableBossInfo();
            }
        } else {
            data.setRetcode(1);
        }

        this.setData(data);
    }
}
