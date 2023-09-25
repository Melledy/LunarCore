package emu.lunarcore.server.packet.send;

import java.util.Collection;

import emu.lunarcore.game.inventory.GameItem;
import emu.lunarcore.proto.AvatarExpUpScRspOuterClass.AvatarExpUpScRsp;
import emu.lunarcore.server.packet.BasePacket;
import emu.lunarcore.server.packet.CmdId;

public class PacketAvatarExpUpScRsp extends BasePacket {

    public PacketAvatarExpUpScRsp(Collection<GameItem> returnItems) {
        super(CmdId.AvatarExpUpScRsp);

        var data = AvatarExpUpScRsp.newInstance();

        for (GameItem item : returnItems) {
            data.addReturnItemList(item.toPileProto());
        }

        this.setData(data);
    }

    public PacketAvatarExpUpScRsp() {
        super(CmdId.AvatarExpUpScRsp);

        var data = AvatarExpUpScRsp.newInstance().setRetcode(1);

        this.setData(data);
    }
}
