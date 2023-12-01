package emu.lunarcore.data.excel;

import emu.lunarcore.data.GameResource;
import emu.lunarcore.data.ResourceType;
import lombok.Getter;

@Getter
@ResourceType(name = {"ChallengeMazeRewardLine.json"})
public class ChallengeRewardExcel extends GameResource {
    private int GroupID;
    private int StarCount;
    private int RewardID;

    @Override
    public int getId() {
        return (GroupID << 16) + StarCount;
    }
}
