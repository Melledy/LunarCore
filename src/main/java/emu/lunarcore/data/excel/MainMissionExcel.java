package emu.lunarcore.data.excel;

import emu.lunarcore.data.resource.GameResource;
import emu.lunarcore.data.resource.ResourceType;
import lombok.Getter;

@Getter
@ResourceType(name = {"MainMission.json"})
public class MainMissionExcel extends GameResource {
    private int MainMissionID;
    private int RewardID;
    private int NextTrackMainMission;
    private String Type;

    @Override
    public int getId() {
        return MainMissionID;
    }
}
