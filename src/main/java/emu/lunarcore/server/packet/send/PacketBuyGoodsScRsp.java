package emu.lunarcore.server.packet.send;

import java.util.List;

import emu.lunarcore.game.inventory.GameItem;
import emu.lunarcore.proto.BuyGoodsCsReqOuterClass.BuyGoodsCsReq;
import emu.lunarcore.proto.BuyGoodsScRspOuterClass.BuyGoodsScRsp;
import emu.lunarcore.proto.ItemListOuterClass.ItemList;
import emu.lunarcore.server.packet.BasePacket;
import emu.lunarcore.server.packet.CmdId;

public class PacketBuyGoodsScRsp extends BasePacket {

    public PacketBuyGoodsScRsp(BuyGoodsCsReq req, List<GameItem> items) {
        super(CmdId.BuyGoodsScRsp);
        
        var data = BuyGoodsScRsp.newInstance();
        
        if (items != null) {
            ItemList returnItems = ItemList.newInstance();
            items.stream().map(GameItem::toProto).forEach(returnItems::addItemList);
            
            data.setShopId(req.getShopId());
            data.setGoodsId(req.getGoodsId());
            data.setGoodsBuyTimes(req.getGoodsNum());
            data.setReturnItemList(returnItems);
        } else {
            data.setRetcode(1);
        }
        
        this.setData(data);
    }
}
