package emu.lunarcore.data.excel;

import emu.lunarcore.GameConstants;
import emu.lunarcore.data.resource.GameResource;
import emu.lunarcore.data.resource.ResourceType;
import emu.lunarcore.game.inventory.ItemParamMap;
import lombok.Getter;

@Getter
@ResourceType(name = {"RewardData.json"})
public class RewardExcel extends GameResource {
    private int RewardID;

    private int Hcoin;

    private int ItemID_1;
    private int Count_1;
    private int ItemID_2;
    private int Count_2;
    private int ItemID_3;
    private int Count_3;
    private int ItemID_4;
    private int Count_4;
    private int ItemID_5;
    private int Count_5;

    private transient ItemParamMap rewards;

    @Override
    public int getId() {
        return RewardID;
    }

    @Override
    public void onLoad() {
        this.rewards = new ItemParamMap();

        if (Hcoin > 0) {
            this.rewards.add(GameConstants.MATERIAL_HCOIN_ID, Hcoin);
        }

        if (ItemID_1 > 0) {
            this.rewards.add(ItemID_1, Count_1);
        } if (ItemID_2 > 0) {
            this.rewards.add(ItemID_2, Count_2);
        } if (ItemID_3 > 0) {
            this.rewards.add(ItemID_3, Count_3);
        } if (ItemID_4 > 0) {
            this.rewards.add(ItemID_4, Count_4);
        } if (ItemID_5 > 0) {
            this.rewards.add(ItemID_5, Count_5);
        }
    }
}
