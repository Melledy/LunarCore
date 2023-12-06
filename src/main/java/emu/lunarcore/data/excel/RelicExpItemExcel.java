package emu.lunarcore.data.excel;

import java.util.Comparator;

import emu.lunarcore.data.GameData;
import emu.lunarcore.data.GameDepot;
import emu.lunarcore.data.GameResource;
import emu.lunarcore.data.ResourceType;
import emu.lunarcore.data.ResourceType.LoadPriority;
import emu.lunarcore.game.enums.ItemRarity;
import lombok.Getter;

@Getter
@ResourceType(name = {"RelicExpItem.json"}, loadPriority = LoadPriority.LOW)
public class RelicExpItemExcel extends GameResource {
    private int ItemID;
    private int ExpProvide;
    private int CoinCost;

    @Override
    public int getId() {
        return ItemID;
    }

    @Override
    public void onLoad() {
        // Set exp for this item
        ItemExcel excel = GameData.getItemExcelMap().get(ItemID);
        if (excel == null) return;

        excel.setRelicExp(ExpProvide);
        excel.setExpCost(CoinCost);

        // Add to game depot
        if (ExpProvide > 0 && excel.getRarity() != ItemRarity.SuperRare) {
            GameDepot.getRelicExpExcels().add(this);
            GameDepot.getRelicExpExcels().sort(new Comparator<RelicExpItemExcel>() {
                @Override
                public int compare(RelicExpItemExcel o1, RelicExpItemExcel o2) {
                    return o2.getExpProvide() - o1.getExpProvide();
                }
            });
        }
    }
}
