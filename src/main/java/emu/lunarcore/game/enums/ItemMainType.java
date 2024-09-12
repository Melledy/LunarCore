package emu.lunarcore.game.enums;

import emu.lunarcore.game.inventory.tabs.InventoryTabType;
import lombok.Getter;

@Getter
public enum ItemMainType {
    Unknown     (0),
    Virtual     (1),
    AvatarCard  (2),
    Equipment   (3, InventoryTabType.EQUIPMENT),
    Relic       (4, InventoryTabType.RELIC),
    Usable      (5, InventoryTabType.MATERIAL),
    Material    (6, InventoryTabType.MATERIAL),
    Mission     (7, InventoryTabType.MATERIAL),
    Display     (8),
    Pet         (9);

    private int val;
    private InventoryTabType tabType;

    private ItemMainType(int value) {
        this(value, InventoryTabType.NONE);
    }
    
    private ItemMainType(int value, InventoryTabType tabType) {
        this.val = value;
        this.tabType = tabType;
    }
}
