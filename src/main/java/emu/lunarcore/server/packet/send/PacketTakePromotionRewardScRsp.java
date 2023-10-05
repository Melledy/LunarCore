package emu.lunarcore.server.packet.send;

import java.util.Collection;

import emu.lunarcore.game.inventory.GameItem;
import emu.lunarcore.proto.ItemListOuterClass.ItemList;
import emu.lunarcore.proto.TakePromotionRewardScRspOuterClass.TakePromotionRewardScRsp;
import emu.lunarcore.server.packet.BasePacket;
import emu.lunarcore.server.packet.CmdId;

public class PacketTakePromotionRewardScRsp extends BasePacket {
    
    public PacketTakePromotionRewardScRsp() {
        super(CmdId.TakePromotionRewardScRsp);
        
        var data = TakePromotionRewardScRsp.newInstance()
                .setRetcode(1);
        
        this.setData(data);
    }

    public PacketTakePromotionRewardScRsp(Collection<GameItem> rewards) {
        super(CmdId.TakePromotionRewardScRsp);
        
        var rewardList = ItemList.newInstance();
        
        for (GameItem item : rewards) {
            rewardList.addItemList(item.toProto());
        }
        
        var data = TakePromotionRewardScRsp.newInstance()
                .setRewardList(rewardList);
        
        this.setData(data);
    }

}
