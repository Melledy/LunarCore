package emu.lunarcore.data.excel;

import emu.lunarcore.data.resource.GameResource;
import emu.lunarcore.data.resource.ResourceType;
import lombok.Getter;

@Getter
@ResourceType(name = {"ChallengeGroupConfig.json", "ChallengeStoryGroupConfig.json"})
public class ChallengeGroupExcel extends GameResource {
    private int GroupID;
    private int RewardLineGroupID;

    @Override
    public int getId() {
        return GroupID;
    }
}
