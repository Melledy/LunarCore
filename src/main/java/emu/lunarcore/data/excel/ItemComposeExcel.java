package emu.lunarcore.data.excel;

import java.util.List;

import emu.lunarcore.data.GameResource;
import emu.lunarcore.data.ResourceType;
import emu.lunarcore.data.common.ItemParam;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import lombok.Getter;

@Getter
@ResourceType(name = {"ItemComposeConfig.json"})
public class ItemComposeExcel extends GameResource {
    private int ID;
    private int ItemID;
    private FormulaType FormulaType;
    private List<ItemParam> MaterialCost;
    private IntOpenHashSet SpecialMaterialCost;
    private int SpecialMaterialCostNumber;
    private int CoinCost;
    private int WorldLevelRequire;
    private IntOpenHashSet RelicList;

    @Override
    public int getId() {
        return ID;
    }

    public enum FormulaType {
        Unknown, Normal, Sepcial, SelectedRelic;
    }
}
