package emu.lunarcore.game.enums;

import lombok.Getter;

@Getter
public enum RelicType {
    Unknow (0),
    HEAD (1),
    HAND (2),
    BODY (3),
    FOOT (4),
    NECK (5),
    OBJECT (6);

    private int val;

    private RelicType(int value) {
        this.val = value;
    }
}
