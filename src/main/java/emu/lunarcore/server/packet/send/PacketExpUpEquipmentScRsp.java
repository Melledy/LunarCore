package emu.lunarcore.server.packet.send;

import java.util.Collection;

import emu.lunarcore.game.inventory.GameItem;
import emu.lunarcore.proto.ExpUpEquipmentScRspOuterClass.ExpUpEquipmentScRsp;
import emu.lunarcore.server.packet.BasePacket;
import emu.lunarcore.server.packet.CmdId;

public class PacketExpUpEquipmentScRsp extends BasePacket {

    public PacketExpUpEquipmentScRsp(Collection<GameItem> returnItems) {
        super(CmdId.ExpUpEquipmentScRsp);

        var data = ExpUpEquipmentScRsp.newInstance();
        
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
