package emu.lunarcore.server.packet.send;

import java.util.Collection;

import emu.lunarcore.game.inventory.GameItem;
import emu.lunarcore.proto.TakePromotionRewardScRspOuterClass.TakePromotionRewardScRsp;
import emu.lunarcore.server.packet.BasePacket;
import emu.lunarcore.server.packet.CmdId;

public class PacketTakePromotionRewardScRsp extends BasePacket {

    public PacketTakePromotionRewardScRsp(Collection<GameItem> rewards) {
        super(CmdId.TakePromotionRewardScRsp);

        var data = TakePromotionRewardScRsp.newInstance();
        
        if (rewards != null) {
            for (GameItem item : rewards) {
                data.getMutableRewardList().addItemList(item.toProto());
            }
        } else {
            data.setRetcode(1);
        }
        
        this.setData(data);
    }

}
