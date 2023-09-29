package emu.lunarcore.game.enums;

import lombok.Getter;

@Getter
public enum AvatarBaseType {
    Unknown (0),
    Warrior (1),
    Rogue (2),
    Mage (3),
    Shaman (4),
    Warlock (5),
    Knight (6),
    Priest (7);

    private final int val;

    private AvatarBaseType(int value) {
        this.val = value;
    }
}
