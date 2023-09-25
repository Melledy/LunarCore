package emu.lunarcore.data.excel;

import java.util.ArrayList;
import java.util.Iterator;
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
    private transient int PromotionCostCoin;

    private double AttackBase;
    private double AttackAdd;
    private double DefenceBase;
    private double DefenceAdd;
    private double HPBase;
    private double HPAdd;
    private double SpeedBase;
    private double CriticalChance;
    private double CriticalDamage;
    private double BaseAggro;

    @Override
    public int getId() {
        return (AvatarID << 8) + Promotion;
    }

    @Override
    public void onLoad() {
        if (this.PromotionCostList == null) {
            this.PromotionCostList = new ArrayList<>();
        } else {
            Iterator<ItemParam> it = this.PromotionCostList.iterator();
            while (it.hasNext()) {
                ItemParam param = it.next();
                if (param.getId() == 2) {
                    this.PromotionCostCoin = param.getCount();
                    it.remove();
                }
            }
        }
    }
}
