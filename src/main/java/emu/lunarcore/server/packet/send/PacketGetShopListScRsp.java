package emu.lunarcore.server.packet.send;

import emu.lunarcore.data.GameData;
import emu.lunarcore.data.excel.ShopExcel;
import emu.lunarcore.proto.GetShopListScRspOuterClass.GetShopListScRsp;
import emu.lunarcore.proto.ShopOuterClass.Shop;
import emu.lunarcore.server.packet.BasePacket;
import emu.lunarcore.server.packet.CmdId;

public class PacketGetShopListScRsp extends BasePacket {

    public PacketGetShopListScRsp(int shopType) {
        super(CmdId.GetShopListScRsp);
        
        var data = GetShopListScRsp.newInstance()
                .setShopType(shopType);
        
        for (ShopExcel shopExcel : GameData.getShopExcelMap().values()) {
            if (shopExcel.getShopType() != shopType || shopExcel.getGoods().size() == 0) {
                continue;
            }
            
            Shop shop = Shop.newInstance()
                    .setShopId(shopExcel.getId())
                    .setCityLevel(1)
                    .setEndTime(Integer.MAX_VALUE);

            for (var goodsExcel : shopExcel.getGoods().values()) {
                shop.addGoodsList(goodsExcel.toProto());
            }
            
            data.addShopList(shop);
        }
        
        this.setData(data);
    }
}
