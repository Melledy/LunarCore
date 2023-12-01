package emu.lunarcore.data.excel;

import emu.lunarcore.data.GameResource;
import emu.lunarcore.data.ResourceType;
import it.unimi.dsi.fastutil.ints.Int2ObjectAVLTreeMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import lombok.Getter;

@Getter
@ResourceType(name = {"ShopConfig.json"})
public class ShopExcel extends GameResource {
    private int ShopID;
    private int ShopType;

    private transient Int2ObjectMap<ShopGoodsExcel> goods;

    public ShopExcel() {
        this.goods = new Int2ObjectAVLTreeMap<>();
    }

    @Override
    public int getId() {
        return ShopID;
    }

}
