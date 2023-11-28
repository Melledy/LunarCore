package emu.lunarcore.data.excel;

import emu.lunarcore.data.GameData;
import emu.lunarcore.data.GameResource;
import emu.lunarcore.data.ResourceType;
import emu.lunarcore.data.ResourceType.LoadPriority;
import emu.lunarcore.game.enums.RelicType;
import lombok.Getter;

@Getter
@ResourceType(name = {"RelicConfig.json"}, loadPriority = LoadPriority.LOW)
public class RelicExcel extends GameResource {
    private int ID;
    private int SetID;
    private RelicType Type;

    private int MainAffixGroup;
    private int SubAffixGroup;
    private int MaxLevel;
    private int ExpType;

    private int ExpProvide;
    private int CoinCost;

    @Override
    public int getId() {
        return ID;
    }
    public int getSetId() {
        return SetID;
    }

    @Override
    public void onLoad() {
        ItemExcel excel = GameData.getItemExcelMap().get(this.getId());
        if (excel != null) {
            excel.setRelicExcel(this);
        }
    }
}
