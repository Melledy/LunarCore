package emu.lunarcore.server.packet.send;

import java.util.Collection;

import emu.lunarcore.game.inventory.GameItem;
import emu.lunarcore.proto.ComposeSelectedRelicScRspOuterClass.ComposeSelectedRelicScRsp;
import emu.lunarcore.server.packet.BasePacket;
import emu.lunarcore.server.packet.CmdId;

public class PacketComposeSelectedRelicScRsp extends BasePacket {

    public PacketComposeSelectedRelicScRsp(int composeId, Collection<GameItem> returnList) {
        super(CmdId.ComposeSelectedRelicScRsp);
        
        var data = ComposeSelectedRelicScRsp.newInstance()
                .setComposeId(composeId);
        
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
