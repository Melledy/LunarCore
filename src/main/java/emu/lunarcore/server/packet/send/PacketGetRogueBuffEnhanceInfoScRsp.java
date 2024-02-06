package emu.lunarcore.server.packet.send;

import emu.lunarcore.data.excel.RogueBuffExcel;
import emu.lunarcore.game.player.Player;
import emu.lunarcore.proto.GetRogueBuffEnhanceInfoScRspOuterClass.GetRogueBuffEnhanceInfoScRsp;
import emu.lunarcore.proto.ItemCostListOuterClass.ItemCostList;
import emu.lunarcore.proto.ItemCostOuterClass.ItemCost;
import emu.lunarcore.proto.PileItemOuterClass.PileItem;
import emu.lunarcore.proto.RogueBuffEnhanceInfoOuterClass.RogueBuffEnhanceInfo;
import emu.lunarcore.proto.RogueBuffEnhanceShopInfoOuterClass.RogueBuffEnhanceShopInfo;
import emu.lunarcore.server.packet.BasePacket;
import emu.lunarcore.server.packet.CmdId;

public class PacketGetRogueBuffEnhanceInfoScRsp extends BasePacket {
    public PacketGetRogueBuffEnhanceInfoScRsp(Player player) {
        super(CmdId.GetRogueBuffEnhanceInfoScRsp);
        
        var buffs = player.getRogueInstance().getBuffs();
        var proto = GetRogueBuffEnhanceInfoScRsp.newInstance();
        var shop = RogueBuffEnhanceShopInfo.newInstance();
        
        for (var buff : buffs.values()) {
            if (buff.getLevel() > 1) continue;
            shop.addBuffInfo(RogueBuffEnhanceInfo.newInstance()
                .setBuffId(buff.getId())
                .setItemCostList(this.getItemCostList(buff.getExcel()))
                .setHNHFMFCDCOC(1));
        }
        proto.setShopInfo(shop);
        
        this.setData(proto);
    }
    
    public ItemCostList getItemCostList(RogueBuffExcel excel) {
        int cost = 100 + (excel.getRogueBuffRarity() - 1) * 30;
        return ItemCostList.newInstance()
            .addItemList(ItemCost.newInstance()
                .setPileItem(PileItem.newInstance()
                    .setItemId(31)
                    .setItemNum(cost)));
    }
}
