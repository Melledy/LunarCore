package emu.lunarcore.game.enums;

import lombok.Getter;

@Getter
public enum ItemRarity {
    Unknown     (0),
    Normal      (1),
    NotNormal   (2),
    Rare        (3),
    VeryRare    (4),
    SuperRare   (5);

    private int val;

    private ItemRarity(int value) {
        this.val = value;
    }
}
