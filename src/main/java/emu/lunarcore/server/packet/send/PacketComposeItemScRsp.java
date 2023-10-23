package emu.lunarcore.server.packet.send;

import java.util.Collection;

import emu.lunarcore.game.inventory.GameItem;
import emu.lunarcore.proto.ComposeItemScRspOuterClass.ComposeItemScRsp;
import emu.lunarcore.server.packet.BasePacket;
import emu.lunarcore.server.packet.CmdId;

public class PacketComposeItemScRsp extends BasePacket {

    public PacketComposeItemScRsp(int composeId, int count, Collection<GameItem> returnList) {
        super(CmdId.ComposeItemScRsp);

        var data = ComposeItemScRsp.newInstance()
                .setComposeId(composeId)
                .setCount(count);
        
        if (returnList != null) {
            for (var item : returnList) {
                data.getMutableReturnItemList().addItemList(item.toProto());
            }
        } else {
            data.getMutableReturnItemList();
        }
        
        this.setData(data);
    }
}
