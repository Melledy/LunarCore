package emu.lunarcore.game.enums;

import lombok.Getter;

@Getter
public enum MissionStatusType {
    MISSION_NONE     (0),
    MISSION_DOING    (1),
    MISSION_FINISH   (2),
    MISSION_PREPARED (3);

    private final int val;

    private MissionStatusType(int value) {
        this.val = value;
    }
}
