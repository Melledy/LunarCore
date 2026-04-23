package emu.lunarcore.data.config;

import lombok.Getter;

/**
 *  Original name: LevelGroupMissionCondition
 */
@Getter
public class LevelGroupMissionCondition {
    private LevelGroupMissionType Type; // = LevelGroupMissionType.MainMission
    private LevelGroupMissionPhase Phase;
    private int ID;

    public enum LevelGroupMissionType {
        MainMission, SubMission, EventMission;
    }

    public LevelGroupMissionType getMissionType() {
        return this.Type;
    }

    public enum LevelGroupMissionPhase {
        Accept, Finish, Cancel;
    }
}
