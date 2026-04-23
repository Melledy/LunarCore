package emu.lunarcore.data.excel;

import java.util.List;

import emu.lunarcore.data.common.ItemParam;
import emu.lunarcore.data.resource.MultiKeyGameResource;
import emu.lunarcore.data.resource.ResourceType;
import emu.lunarcore.data.resource.ResourceType.LoadPriority;
import lombok.Getter;

@Getter
@ResourceType(name = {"AvatarPromotionConfig.json", "AvatarPromotionConfigLD.json"}, loadPriority = LoadPriority.HIGHEST)
public class AvatarPromotionExcel extends MultiKeyGameResource {
    private int AvatarID;
    private int Promotion;

    private int MaxLevel;
    private int PlayerLevelRequire;
    private int WorldLevelRequire;
    private List<ItemParam> PromotionCostList;

    @Override
    public int getPrimaryKey() {
        return AvatarID;
    }

    @Override
    public int getSecondaryKey() {
        return Promotion;
    }
}
