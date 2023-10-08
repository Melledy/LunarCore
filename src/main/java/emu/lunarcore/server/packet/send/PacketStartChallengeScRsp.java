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
        
        var data = StartChallengeScRsp.newInstance();
        
        if (player.getChallengeData() != null) {
            data.setLineup(player.getCurrentLineup().toProto());
            data.setScene(player.getScene().toProto());
            data.setChallengeInfo(player.getChallengeData().toProto());
        } else {
            data.setRetcode(1);
        }
        
        this.setData(data);
    }
}
