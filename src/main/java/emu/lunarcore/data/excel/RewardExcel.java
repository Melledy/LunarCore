package emu.lunarcore.data.excel;

import java.util.ArrayList;
import java.util.List;

import emu.lunarcore.GameConstants;
import emu.lunarcore.data.GameResource;
import emu.lunarcore.data.ResourceType;
import emu.lunarcore.data.common.ItemParam;
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

    private transient List<ItemParam> rewards;

    @Override
    public int getId() {
        return RewardID;
    }

    @Override
    public void onLoad() {
        this.rewards = new ArrayList<>();

        if (Hcoin > 0) {
            this.rewards.add(new ItemParam(GameConstants.MATERIAL_HCOIN_ID, Hcoin));
        }

        if (ItemID_1 > 0) {
            this.rewards.add(new ItemParam(ItemID_1, Count_1));
        } if (ItemID_2 > 0) {
            this.rewards.add(new ItemParam(ItemID_2, Count_2));
        } if (ItemID_3 > 0) {
            this.rewards.add(new ItemParam(ItemID_3, Count_3));
        } if (ItemID_4 > 0) {
            this.rewards.add(new ItemParam(ItemID_4, Count_4));
        } if (ItemID_5 > 0) {
            this.rewards.add(new ItemParam(ItemID_5, Count_5));
        }
    }
}
