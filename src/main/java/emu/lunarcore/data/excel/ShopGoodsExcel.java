package emu.lunarcore.data.excel;

import emu.lunarcore.data.GameData;
import emu.lunarcore.data.GameResource;
import emu.lunarcore.data.ResourceType;
import emu.lunarcore.data.ResourceType.LoadPriority;
import emu.lunarcore.proto.GoodsOuterClass.Goods;
import lombok.Getter;

@Getter
@ResourceType(name = {"ShopGoodsConfig.json"}, loadPriority = LoadPriority.LOW)
public class ShopGoodsExcel extends GameResource {
    private int GoodsID;
    private int ItemID;
    private int ItemCount;
    private int ShopID;
    private int[] CurrencyList;
    private int[] CurrencyCostList;
    
    @Override
    public int getId() {
        return GoodsID;
    }
    
    @Override
    public void onLoad() {
        ShopExcel shop = GameData.getShopExcelMap().get(this.ShopID);
        if (shop == null) return;
        
        shop.getGoods().put(this.GoodsID, this);
    }

    public Goods toProto() {
        var proto = Goods.newInstance()
                .setGoodsId(this.getGoodsID())
                .setItemId(this.getItemID())
                .setEndTime(Integer.MAX_VALUE);
        
        return proto;
    }
}
