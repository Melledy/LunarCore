package emu.lunarcore.data.excel;

import java.util.List;

import emu.lunarcore.data.GameData;
import emu.lunarcore.data.GameDepot;
import emu.lunarcore.data.common.ItemParam;
import emu.lunarcore.data.resource.MultiKeyGameResource;
import emu.lunarcore.data.resource.ResourceType;
import emu.lunarcore.data.resource.ResourceType.LoadPriority;
import emu.lunarcore.game.avatar.AvatarSkillTreeAnchorType;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import lombok.Getter;

@Getter
@ResourceType(name = {"AvatarSkillTreeConfig.json", "AvatarSkillTreeConfigLD.json"}, loadPriority = LoadPriority.LOW)
public class AvatarSkillTreeExcel extends MultiKeyGameResource {
    private int PointID;
    private int Level;
    private int MaxLevel;
    private int EnhancedID;
    private String AnchorType;
    private boolean DefaultUnlock;

    private int AvatarID;
    private int AvatarPromotionLimit;
    private int AvatarLevelLimit;

    private List<ItemParam> MaterialList;
    private IntArrayList PrePoint;
    private IntArrayList LevelUpSkillID;
    
    private transient int anchorId;

    @Override
    public int getPrimaryKey() {
        return PointID;
    }

    @Override
    public int getSecondaryKey() {
        return Level;
    }

    @Override
    public void onLoad() {
        // Cache anchor id
        this.anchorId = AvatarSkillTreeAnchorType.getValue(this.getAnchorType());
        
        if (this.anchorId > 0) {
            long key = ((long) this.getAvatarID() << 48) + ((long) this.getEnhancedID() << 32) + ((long) this.getAnchorId() << 16) + (long) this.getLevel();
            GameDepot.getAvatarSkillTreeExcels().put(key, this);
        }
        
        // Load to excel
        AvatarExcel excel = GameData.getAvatarExcelMap().get(this.AvatarID);
        if (excel == null) return;

        // Add to default avatar skills
        if (this.isDefaultUnlock()) {
            excel.getDefaultSkillTrees().add(this);
        }

        // Cache to avatar excel
        excel.getSkillTreeIds().add(this.getPointID());
    }
}
