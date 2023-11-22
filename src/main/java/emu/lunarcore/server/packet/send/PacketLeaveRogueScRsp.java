package emu.lunarcore.server.packet.send;

import emu.lunarcore.game.player.Player;
import emu.lunarcore.proto.LeaveRogueScRspOuterClass.LeaveRogueScRsp;
import emu.lunarcore.server.packet.BasePacket;
import emu.lunarcore.server.packet.CmdId;

public class PacketLeaveRogueScRsp extends BasePacket {

    public PacketLeaveRogueScRsp(Player player) {
        super(CmdId.LeaveRogueScRsp);
        
        var data = LeaveRogueScRsp.newInstance()
                .setLineup(player.getCurrentLineup().toProto())
                .setRogueInfo(player.getRogueManager().toProto())
                .setScene(player.getScene().toProto());
        
        this.setData(data);
    }
}
