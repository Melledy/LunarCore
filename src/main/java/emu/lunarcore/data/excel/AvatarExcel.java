package emu.lunarcore.data.excel;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import emu.lunarcore.data.GameData;
import emu.lunarcore.data.resource.GameResource;
import emu.lunarcore.data.resource.ResourceType;
import emu.lunarcore.game.battle.skills.MazeSkill;
import emu.lunarcore.game.enums.AvatarBaseType;
import emu.lunarcore.game.enums.DamageType;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Getter
@ResourceType(name = {"AvatarConfig.json", "AvatarConfigLD.json"})
public class AvatarExcel extends GameResource {
    private int AvatarID;
    private long AvatarName;
    private DamageType DamageType;
    private AvatarBaseType AvatarBaseType;

    private int ExpGroup;
    private int MaxPromotion;
    private int MaxRank;

    private int[] RankIDList;
    private int[] SkillList;
    private String ManikinJsonPath;

    @Getter(AccessLevel.NONE)
    private transient AvatarPromotionExcel[] promotionData;
    private transient List<AvatarSkillTreeExcel> defaultSkillTrees;
    private transient IntSet skillTreeIds;
    private transient String nameKey;

    @Setter private transient MazeSkill mazeAttack;
    @Setter private transient MazeSkill mazeSkill;

    private static Pattern namePattern = Pattern.compile("(?<=Avatar_)(.*?)(?=_Config)");

    public AvatarExcel() {
        this.defaultSkillTrees = new ObjectArrayList<>();
        this.skillTreeIds = new IntOpenHashSet();
    }

    @Override
    public int getId() {
        return AvatarID;
    }

    public int getRankUpItemId() {
        // Hacky fix so we dont have to fetch data from an excel
        return this.AvatarID + 10000;
    }

    public AvatarPromotionExcel getPromotionData(int i) {
        return this.promotionData[i];
    }

    public int getRankId(int rank) {
        return RankIDList[Math.min(rank, RankIDList.length - 1)];
    }

    public boolean isMultiPathAvatar() {
        var excel = GameData.getMultiplePathAvatarExcelMap().get(AvatarID);
        if (excel == null) {
            return false;
        }

        return excel.getAvatarID() != excel.getBaseAvatarID();
    }

    @Override
    public void onLoad() {
        // Load promotion data
        this.promotionData = new AvatarPromotionExcel[MaxPromotion + 1];

        for (int i = 0; i <= MaxPromotion; i++) {
            this.promotionData[i] = GameData.getAvatarPromotionExcelMap().get(getId(), i);
        }

        // Get name key
        Matcher matcher = namePattern.matcher(this.ManikinJsonPath);

        if (matcher.find()) {
            this.nameKey = matcher.group(0);
        }

        // Clear variable to save memory
        this.ManikinJsonPath = null;
    }
}
