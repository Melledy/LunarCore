package emu.lunarcore.data.excel;

import java.util.List;

import emu.lunarcore.data.GameResource;
import emu.lunarcore.data.ResourceType;
import emu.lunarcore.data.ResourceType.LoadPriority;
import emu.lunarcore.data.common.ItemParam;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import lombok.Getter;

@Getter
@ResourceType(name = {"AvatarRankConfig.json"}, loadPriority = LoadPriority.HIGHEST)
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
