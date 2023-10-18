package emu.lunarcore.server.packet.send;

import java.util.Collection;

import emu.lunarcore.game.inventory.GameItem;
import emu.lunarcore.proto.TakeMailAttachmentScRspOuterClass.TakeMailAttachmentScRsp;
import emu.lunarcore.server.packet.BasePacket;
import emu.lunarcore.server.packet.CmdId;
import us.hebi.quickbuf.RepeatedInt;

public class PacketTakeMailAttachmentScRsp extends BasePacket {

    public PacketTakeMailAttachmentScRsp(RepeatedInt idList, Collection<GameItem> items) {
        super(CmdId.TakeMailAttachmentScRsp);
        
        var data = TakeMailAttachmentScRsp.newInstance();
        
        for (int id : idList) {
            data.addSuccMailIdList(id);
        }
        
        for (GameItem item : items) {
            data.getMutableAttachment().addItemList(item.toProto());
        }
        
        this.setData(data);
    }
}
