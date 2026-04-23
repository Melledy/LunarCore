package emu.lunarcore.game.enums;

import lombok.Getter;

@Getter
public enum RogueMiracleCategory {
    None (0),
    Common (1),
    Rare (2),
    Legendary (3),
    Negative (4),
    Hex (5);
    
    private final int val;

    private RogueMiracleCategory(int value) {
        this.val = value;
    }
}
