package emu.lunarcore.server.packet.send;

import java.util.Collection;

import emu.lunarcore.game.inventory.GameItem;
import emu.lunarcore.proto.SellItemScRspOuterClass.SellItemScRsp;
import emu.lunarcore.server.packet.BasePacket;
import emu.lunarcore.server.packet.CmdId;

public class PacketSellItemScRsp extends BasePacket {

    public PacketSellItemScRsp(Collection<GameItem> returnItems) {
        super(CmdId.SellItemScRsp);

        var data = SellItemScRsp.newInstance();

        if (returnItems != null) {
            for (var item : returnItems) {
                data.getMutableReturnItemList().addItemList(item.toProto());
            }
        } else {
            data.setRetcode(1);
        }

        this.setData(data);
    }
}
