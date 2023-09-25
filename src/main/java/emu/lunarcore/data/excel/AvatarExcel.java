package emu.lunarcore.data.excel;

import java.util.ArrayList;
import java.util.List;

import emu.lunarcore.data.GameData;
import emu.lunarcore.data.GameResource;
import emu.lunarcore.data.ResourceType;
import emu.lunarcore.game.avatar.AvatarBaseType;
import emu.lunarcore.game.avatar.DamageType;
import lombok.AccessLevel;
import lombok.Getter;

@Getter
@ResourceType(name = {"AvatarConfig.json"})
public class AvatarExcel extends GameResource {
    private int AvatarID;
    private long AvatarName;
    private DamageType DamageType;
    private AvatarBaseType AvatarBaseType;
    private double SPNeed;

    private int ExpGroup;
    private int MaxPromotion;
    private int MaxRank;

    private int[] RankIDList;
    private int[] SkillList;

    @Getter(AccessLevel.NONE)
    private transient AvatarPromotionExcel[] promotionData;
    private transient List<AvatarSkillTreeExcel> defaultSkillTrees;
    private transient int maxSp;

    public AvatarExcel() {
        this.defaultSkillTrees = new ArrayList<>();
    }

    @Override
    public int getId() {
        return AvatarID;
    }

    public AvatarPromotionExcel getPromotionData(int i) {
        return this.promotionData[i];
    }

    public int getRankId(int rank) {
        return RankIDList[Math.min(rank, RankIDList.length - 1)];
    }

    @Override
    public void onLoad() {
        // Load promotion data
        this.promotionData = new AvatarPromotionExcel[MaxPromotion + 1];

        for (int i = 0; i <= MaxPromotion; i++) {
            this.promotionData[i] = GameData.getAvatarPromotionExcel(getId(), i);
        }

        // Cache max sp
        this.maxSp = (int) this.SPNeed * 100;
    }
}
