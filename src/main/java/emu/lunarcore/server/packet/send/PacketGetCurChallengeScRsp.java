package emu.lunarcore.server.packet.send;

import emu.lunarcore.game.player.Player;
import emu.lunarcore.proto.GetCurChallengeScRspOuterClass.GetCurChallengeScRsp;
import emu.lunarcore.server.packet.BasePacket;
import emu.lunarcore.server.packet.CmdId;

public class PacketGetCurChallengeScRsp extends BasePacket {

    public PacketGetCurChallengeScRsp(Player player) {
        super(CmdId.GetCurChallengeScRsp);
        
        var data = GetCurChallengeScRsp.newInstance();
        
        if (player.getChallengeData() != null) {
            data.setChallengeInfo(player.getChallengeData().toProto());
        } else {
            data.setRetcode(1);
        }
        
        this.setData(data);
    }
}
