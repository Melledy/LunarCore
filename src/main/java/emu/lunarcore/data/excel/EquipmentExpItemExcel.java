package emu.lunarcore.data.excel;

import java.util.Comparator;

import emu.lunarcore.data.GameData;
import emu.lunarcore.data.GameDepot;
import emu.lunarcore.data.GameResource;
import emu.lunarcore.data.ResourceType;
import emu.lunarcore.data.ResourceType.LoadPriority;
import lombok.Getter;

@Getter
@ResourceType(name = {"EquipmentExpItemConfig.json"}, loadPriority = LoadPriority.LOW)
public class EquipmentExpItemExcel extends GameResource {
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

        excel.setEquipmentExp(ExpProvide);
        excel.setExpCost(CoinCost);

        // Add to game depot
        if (ExpProvide > 0) {
            GameDepot.getEquipmentExpExcels().add(this);
            GameDepot.getEquipmentExpExcels().sort(new Comparator<EquipmentExpItemExcel>() {
                @Override
                public int compare(EquipmentExpItemExcel o1, EquipmentExpItemExcel o2) {
                    return o2.getExpProvide() - o1.getExpProvide();
                }
            });
        }
    }
}
