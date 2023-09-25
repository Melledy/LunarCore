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

        for (GameItem item : returnItems) {
            data.addReturnItemList(item.toPileProto());
        }

        this.setData(data);
    }

    public PacketExpUpRelicScRsp() {
        super(CmdId.ExpUpRelicScRsp);

        var data = ExpUpRelicScRsp.newInstance().setRetcode(1);

        this.setData(data);
    }
}
