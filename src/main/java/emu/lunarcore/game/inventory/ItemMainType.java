package emu.lunarcore.game.inventory;

import lombok.Getter;

@Getter
public enum ItemMainType {
    Unknown (0),
    Virtual (1),
    AvatarCard (2),
    Equipment (3),
    Relic (4),
    Usable (5),
    Material (6),
    Mission (7),
    Display (8);

    private int val;

    private ItemMainType(int value) {
        this.val = value;
    }
}
