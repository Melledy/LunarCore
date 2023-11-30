package emu.lunarcore.server.packet.send;

import emu.lunarcore.game.mail.Mail;
import emu.lunarcore.game.player.Player;
import emu.lunarcore.proto.TextJoinQueryScRspOuterClass.TextJoinQueryScRsp;
import emu.lunarcore.server.packet.BasePacket;
import emu.lunarcore.server.packet.CmdId;

public class PacketTextJoinQueryScRsp extends BasePacket {

    public PacketTextJoinQueryScRsp(Player player) {
        super(CmdId.TextJoinQueryScRsp);
        
        var data = TextJoinQueryScRsp.newInstance();
        
        this.setData(data);
    }
}
