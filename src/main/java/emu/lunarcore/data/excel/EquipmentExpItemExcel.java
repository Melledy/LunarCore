package emu.lunarcore.data.excel;

import emu.lunarcore.data.GameData;
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
        ItemExcel excel = GameData.getItemExcelMap().get(ItemID);
        if (excel == null) return;

        excel.setEquipmentExp(ExpProvide);
        excel.setExpCost(CoinCost);
    }
}
