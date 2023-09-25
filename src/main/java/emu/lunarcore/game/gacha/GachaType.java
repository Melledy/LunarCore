package emu.lunarcore.game.gacha;

import lombok.Getter;

@Getter
public enum GachaType {
    Newbie      (101, 1, 2),
    Normal      (101, 1, 2),
    AvatarUp    (102, 1, 1),
    WeaponUp    (102, 2, 2);

    private int costItem;
    private int minItemType;
    private int maxItemType;

    private GachaType(int costItem, int min, int max) {
        this.costItem = costItem;
        this.minItemType = min;
        this.maxItemType = max;
    }
}
