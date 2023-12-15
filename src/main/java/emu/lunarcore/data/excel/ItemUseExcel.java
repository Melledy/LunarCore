package emu.lunarcore.data.excel;

import emu.lunarcore.data.GameResource;
import emu.lunarcore.data.ResourceType;
import emu.lunarcore.game.enums.ItemFoodTargetType;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import lombok.Getter;

@Getter
@ResourceType(name = {"ItemUseData.json", "ItemUseBuffData.json"})
public class ItemUseExcel extends GameResource {
    private int UseDataID;
    private int ConsumeType;
    private int MazeBuffID;
    private int ActivityCount;
    
    private double PreviewHPRecoveryPercent;
    private int PreviewHPRecoveryValue;
    private double PreviewPowerPercent;
    private int PreviewSkillPoint;
    
    private ItemFoodTargetType UseTargetType;
    private IntArrayList UseParam;

    @Override
    public int getId() {
        return UseDataID;
    }

}
