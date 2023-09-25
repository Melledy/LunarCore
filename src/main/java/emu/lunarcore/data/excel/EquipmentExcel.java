package emu.lunarcore.data.excel;

import emu.lunarcore.data.GameData;
import emu.lunarcore.data.GameResource;
import emu.lunarcore.data.ResourceType;
import emu.lunarcore.data.ResourceType.LoadPriority;
import emu.lunarcore.game.inventory.GameItem;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import lombok.Getter;

@Getter
@ResourceType(name = {"EquipmentConfig.json"}, loadPriority = LoadPriority.LOW)
public class EquipmentExcel extends GameResource {
    private int EquipmentID;

    private int MaxPromotion;
    private int MaxRank;
    private int ExpType;
    private int ExpProvide;
    private int CoinCost;

    private IntOpenHashSet RankUpCostList;

    @Override
    public int getId() {
        return EquipmentID;
    }

    public boolean isRankUpItem(GameItem item) {
        return item.getItemId() == this.EquipmentID || RankUpCostList.contains(item.getItemId());
    }

    @Override
    public void onLoad() {
        ItemExcel excel = GameData.getItemExcelMap().get(this.getId());
        if (excel != null) {
            excel.setEquipmentExcel(this);
        }
    }

}
