package emu.lunarcore.server.packet.send;

import java.util.Collection;

import emu.lunarcore.game.inventory.GameItem;
import emu.lunarcore.game.mail.Mail;
import emu.lunarcore.proto.TakeMailAttachmentScRspOuterClass.TakeMailAttachmentScRsp;
import emu.lunarcore.server.packet.BasePacket;
import emu.lunarcore.server.packet.CmdId;

public class PacketTakeMailAttachmentScRsp extends BasePacket {

    public PacketTakeMailAttachmentScRsp(Collection<Mail> mailList) {
        super(CmdId.TakeMailAttachmentScRsp);
        
        var data = TakeMailAttachmentScRsp.newInstance();
        
        for (Mail mail : mailList) {
            data.addSuccMailIdList(mail.getUniqueId());
            
            if (mail.getAttachments() != null) {
                for (GameItem item : mail.getAttachments()) {
                    data.getMutableAttachment().addItemList(item.toProto());
                }
            }
        }
        
        this.setData(data);
    }
}
