package emu.lunarcore.game.enums;

import lombok.Getter;

public enum PropState {
    Closed (0),
    Open (1),
    Locked (2),
    BridgeState1 (3),
    BridgeState2 (4),
    BridgeState3 (5),
    BridgeState4 (6),
    CheckPointDisable (7),
    CheckPointEnable (8),
    TriggerDisable (9),
    TriggerEnable (10),
    ChestLocked (11),
    ChestClosed (12),
    ChestUsed (13),
    Elevator1 (14),
    Elevator2 (15),
    Elevator3 (16),
    WaitActive (17),
    EventClose (18),
    EventOpen (19),
    Hidden (20),
    TeleportGate0 (21),
    TeleportGate1 (22),
    TeleportGate2 (23),
    TeleportGate3 (24),
    Destructed (25),
    CustomState01 (101),
    CustomState02 (102),
    CustomState03 (103),
    CustomState04 (104),
    CustomState05 (105),
    CustomState06 (106),
    CustomState07 (107),
    CustomState08 (108),
    CustomState09 (109);

    @Getter
    private final int val;
    
    private PropState(int val) {
        this.val = val;
    }
}
