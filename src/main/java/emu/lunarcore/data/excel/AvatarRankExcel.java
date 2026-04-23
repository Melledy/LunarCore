package emu.lunarcore.data.excel;

import java.util.List;

import emu.lunarcore.data.common.ItemParam;
import emu.lunarcore.data.resource.GameResource;
import emu.lunarcore.data.resource.ResourceType;
import emu.lunarcore.data.resource.ResourceType.LoadPriority;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import lombok.Getter;

@Getter
@ResourceType(name = {"AvatarRankConfig.json", "AvatarRankConfigLD.json"}, loadPriority = LoadPriority.HIGHEST)
public class AvatarRankExcel extends GameResource {
    private int RankID;
    private int Rank;

    private Int2IntOpenHashMap SkillAddLevelList;
    private List<ItemParam> UnlockCost;

    @Override
    public int getId() {
        return RankID;
    }

    @Override
    public void onLoad() {

    }
}
