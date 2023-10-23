package emu.lunarcore.game.enums;

import lombok.Getter;

@Getter
public enum PlaneType {
    Unknown (0),
    Town (1),
    Maze (2),
    Train (3),
    Challenge (4),
    Rogue (5),
    Raid (6),
    AetherDivide (7),
    TrialActivity (8);

    private final int val;

    private PlaneType(int value) {
        this.val = value;
    }
}