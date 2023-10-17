package emu.lunarcore.server.packet.send;

import emu.lunarcore.game.mail.Mail;
import emu.lunarcore.game.player.Player;
import emu.lunarcore.proto.GetMailScRspOuterClass.GetMailScRsp;
import emu.lunarcore.server.packet.BasePacket;
import emu.lunarcore.server.packet.CmdId;

public class PacketGetMailScRsp extends BasePacket {

    public PacketGetMailScRsp(Player player) {
        super(CmdId.GetMailScRsp);
        
        var data = GetMailScRsp.newInstance()
                .setIsEnd(true)
                .setTotalNum(player.getMailbox().size());
        
        for (Mail mail : player.getMailbox()) {
            data.addMailList(mail.toProto());
        }
        
        this.setData(data);
    }
}
