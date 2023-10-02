package emu.lunarcore.server.packet.send;

import emu.lunarcore.game.player.Player;
import emu.lunarcore.proto.ChallengeInfoOuterClass.ChallengeInfo;
import emu.lunarcore.proto.ChallengeStatusOuterClass.ChallengeStatus;
import emu.lunarcore.proto.ExtraLineupTypeOuterClass.ExtraLineupType;
import emu.lunarcore.proto.StartChallengeScRspOuterClass.StartChallengeScRsp;
import emu.lunarcore.server.packet.BasePacket;
import emu.lunarcore.server.packet.CmdId;

public class PacketStartChallengeScRsp extends BasePacket {
    
    public PacketStartChallengeScRsp() {
        super(CmdId.StartChallengeScRsp);
        
        var data = StartChallengeScRsp.newInstance()
                .setRetcode(1);
        
        this.setData(data);
    }

    public PacketStartChallengeScRsp(Player player, int challengeId) {
        super(CmdId.StartChallengeScRsp);
        
        var challengeInfo = ChallengeInfo.newInstance()
                .setChallengeId(challengeId)
                .setStatus(ChallengeStatus.CHALLENGE_DOING)
                .setExtraLineupType(ExtraLineupType.LINEUP_CHALLENGE);
        
        var data = StartChallengeScRsp.newInstance()
                .setLineup(player.getCurrentLineup().toProto().setExtraLineupType(ExtraLineupType.LINEUP_CHALLENGE)) // TODO temporary
                .setScene(player.getScene().toProto())
                .setChallengeInfo(challengeInfo);
        
        this.setData(data);
    }
}
