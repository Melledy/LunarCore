package emu.lunarcore.data.excel;

import java.util.List;

import emu.lunarcore.data.GameResource;
import emu.lunarcore.data.ResourceType;
import emu.lunarcore.data.ResourceType.LoadPriority;
import emu.lunarcore.data.common.ItemParam;
import lombok.Getter;

@Getter
@ResourceType(name = {"AvatarPromotionConfig.json"}, loadPriority = LoadPriority.HIGHEST)
public class AvatarPromotionExcel extends GameResource {
    private int AvatarID;
    private int Promotion;

    private int MaxLevel;
    private int PlayerLevelRequire;
    private int WorldLevelRequire;
    private List<ItemParam> PromotionCostList;

    @Override
    public int getId() {
        return (AvatarID << 8) + Promotion;
    }
}
