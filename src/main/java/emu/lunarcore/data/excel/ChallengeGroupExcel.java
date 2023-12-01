package emu.lunarcore.data.excel;

import emu.lunarcore.data.GameResource;
import emu.lunarcore.data.ResourceType;
import lombok.Getter;

@Getter
@ResourceType(name = {"ChallengeGroupConfig.json"})
public class ChallengeGroupExcel extends GameResource {
    private int GroupID;
    private int RewardLineGroupID;

    @Override
    public int getId() {
        return GroupID;
    }
}
