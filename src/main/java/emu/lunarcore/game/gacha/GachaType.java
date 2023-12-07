package emu.lunarcore.game.gacha;

import lombok.Getter;

@Getter
public enum GachaType {
    Unknown     (0, 0, 0, 0),
    Newbie      (1, 101, 1, 2),
    Normal      (2, 101, 1, 2),
    AvatarUp    (11, 102, 1, 1),
    WeaponUp    (12, 102, 2, 2);

    private int id;
    private int costItem;
    private int minItemType;
    private int maxItemType;

    private GachaType(int id, int costItem, int min, int max) {
        this.id = id;
        this.costItem = costItem;
        this.minItemType = min;
        this.maxItemType = max;
    }
}
