package emu.lunarcore.data.excel;

import java.util.List;

import emu.lunarcore.data.GameData;
import emu.lunarcore.data.GameResource;
import emu.lunarcore.data.ResourceType;
import emu.lunarcore.data.ResourceType.LoadPriority;
import emu.lunarcore.data.common.ItemParam;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import lombok.Getter;

@Getter
@ResourceType(name = {"AvatarSkillTreeConfig.json"}, loadPriority = LoadPriority.LOW)
public class AvatarSkillTreeExcel extends GameResource {
    private int PointID;
    private int Level;
    private int MaxLevel;
    private boolean DefaultUnlock;

    private int AvatarID;
    private int AvatarPromotionLimit;
    private int AvatarLevelLimit;

    private List<ItemParam> MaterialList;
    private IntArrayList PrePoint;
    private IntArrayList LevelUpSkillID;

    @Override
    public int getId() {
        return (PointID << 4) + Level;
    }

    @Override
    public void onLoad() {
        // Load to excel
        AvatarExcel excel = GameData.getAvatarExcelMap().get(this.AvatarID);
        if (excel == null) return;

        // Add to default avatar skills
        if (this.isDefaultUnlock()) {
            excel.getDefaultSkillTrees().add(this);
        }

        // Add point id to avatar excel
        excel.getSkillTreeIds().add(this.getPointID());
    }
}
