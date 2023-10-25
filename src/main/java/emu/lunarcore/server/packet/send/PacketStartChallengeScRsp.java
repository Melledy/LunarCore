package emu.lunarcore.server.packet.send;

import emu.lunarcore.game.player.Player;
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
        
        if (player.getChallengeInstance() != null) {
            data.setLineup(player.getCurrentLineup().toProto());
            data.setScene(player.getScene().toProto());
            data.setChallengeInfo(player.getChallengeInstance().toProto());
        } else {
            data.setRetcode(1);
        }
        
        this.setData(data);
    }
}
