package emu.lunarcore.data.excel;

import emu.lunarcore.data.resource.GameResource;
import emu.lunarcore.data.resource.ResourceType;
import lombok.Getter;

@Getter
@ResourceType(name = {"ChallengePeakGroupConfig.json"})
public class ChallengePeakGroupExcel extends GameResource {
    private int ID;
    private int[] PreLevelIDList;
    private int BossLevelID;
    private int RewardGroupID;

    @Override
    public int getId() {
        return ID;
    }
}
