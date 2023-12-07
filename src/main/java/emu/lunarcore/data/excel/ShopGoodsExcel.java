package emu.lunarcore.data.excel;

import java.util.ArrayList;
import java.util.List;

import emu.lunarcore.data.GameData;
import emu.lunarcore.data.GameResource;
import emu.lunarcore.data.ResourceType;
import emu.lunarcore.data.ResourceType.LoadPriority;
import emu.lunarcore.data.common.ItemParam;
import emu.lunarcore.proto.GoodsOuterClass.Goods;
import lombok.AccessLevel;
import lombok.Getter;

@Getter
@ResourceType(name = {"ShopGoodsConfig.json"}, loadPriority = LoadPriority.LOW)
public class ShopGoodsExcel extends GameResource {
    private int GoodsID;
    private int ItemID;
    private int ItemCount;
    private int ShopID;

    @Getter(AccessLevel.NONE)
    private int[] CurrencyList;
    @Getter(AccessLevel.NONE)
    private int[] CurrencyCostList;

    private transient List<ItemParam> costList;

    @Override
    public int getId() {
        return GoodsID;
    }

    @Override
    public void onLoad() {
        // Skip if we dont have an item id associated with this goods excel
        if (this.getItemID() == 0) return;

        // Add to shop excel
        ShopExcel shop = GameData.getShopExcelMap().get(this.ShopID);
        if (shop == null) return;

        shop.getGoods().put(this.GoodsID, this);

        // Cache currency cost
        this.costList = new ArrayList<>(CurrencyList.length);

        for (int i = 0; i < CurrencyList.length; i++) {
            ItemParam param = new ItemParam(CurrencyList[i], CurrencyCostList[i]);
            this.costList.add(param);
        }

        // Done - Clear references to save memory
        this.CurrencyList = null;
        this.CurrencyCostList = null;
    }

    public Goods toProto() {
        var proto = Goods.newInstance()
                .setGoodsId(this.getGoodsID())
                .setItemId(this.getItemID())
                .setEndTime(Integer.MAX_VALUE);

        return proto;
    }
}
