package emu.lunarcore.server.packet.send;

import emu.lunarcore.proto.ItemOuterClass.Item;
import emu.lunarcore.proto.SellItemScRspOuterClass.SellItemScRsp;
import emu.lunarcore.server.packet.BasePacket;
import emu.lunarcore.server.packet.CmdId;
import it.unimi.dsi.fastutil.ints.Int2IntMap;

public class PacketSellItemScRsp extends BasePacket {

    public PacketSellItemScRsp(Int2IntMap returnItems) {
        super(CmdId.SellItemScRsp);

        var data = SellItemScRsp.newInstance();

        if (returnItems != null) {
            for (var item : returnItems.int2IntEntrySet()) {
                var itemProto = Item.newInstance()
                        .setItemId(item.getIntKey())
                        .setNum(item.getIntValue());
                
                data.getMutableReturnItemList().addItemList(itemProto);
            }
        } else {
            data.setRetcode(1);
        }

        this.setData(data);
    }
}
