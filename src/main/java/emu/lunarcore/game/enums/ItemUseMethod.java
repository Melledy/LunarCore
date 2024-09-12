package emu.lunarcore.game.enums;

import lombok.Getter;

@Getter
public enum ItemUseMethod {
    Unknown (0),
    FixedRewardGift (101),
    RandomRewardGift (102),
    PlayerSelectedReward (103),
    TeamFoodBenefit (201),
    TeamSpecificFoodBenefit (202),
    ExternalSystemFoodBenefit (203),
    PlayerSelectedDropGift (301),
    TreasureMap (401),
    Recipe (501),
    PerformanceProp (601),
    MonthlyCard (701),
    BPUnlock68 (702),
    BPUnlock128 (703),
    BPUpgradeFrom68To128 (704),
    AutoConversionItem (801),
    TravelBrochureUse (901),
    TravelBrochurePasterUse (902),
    PetSummonRecall (1001);
    
    private int val;

    private ItemUseMethod(int value) {
        this.val = value;
    }
}
