package emu.lunarcore.server.packet.send;

import java.util.Collection;

import emu.lunarcore.game.inventory.GameItem;
import emu.lunarcore.proto.ExpUpRelicScRspOuterClass.ExpUpRelicScRsp;
import emu.lunarcore.server.packet.BasePacket;
import emu.lunarcore.server.packet.CmdId;

public class PacketExpUpRelicScRsp extends BasePacket {

    public PacketExpUpRelicScRsp(Collection<GameItem> returnItems) {
        super(CmdId.ExpUpRelicScRsp);

        var data = ExpUpRelicScRsp.newInstance();

        if (returnItems != null) {
            for (GameItem item : returnItems) {
                data.addReturnItemList(item.toPileProto());
            }
        } else {
            data.setRetcode(1);
        }
        
        this.setData(data);
    }
}
