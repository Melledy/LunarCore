package emu.lunarcore.server.packet.send;

import emu.lunarcore.game.mail.Mail;
import emu.lunarcore.proto.NewMailScNotifyOuterClass.NewMailScNotify;
import emu.lunarcore.server.packet.BasePacket;
import emu.lunarcore.server.packet.CmdId;

public class PacketNewMailScNotify extends BasePacket {

    public PacketNewMailScNotify(Mail mail) {
        super(CmdId.NewMailScNotify);
        
        var data = NewMailScNotify.newInstance()
                .addMailIdList(mail.getUniqueId());
        
        this.setData(data);
    }
}
