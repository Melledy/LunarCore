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
    Usable      (5),
    Material    (6),
    Mission     (7),
    Display     (8);

    private int val;
    private InventoryTabType tabType = InventoryTabType.MATERIAL;

    private ItemMainType(int value) {
        this.val = value;
    }
    
    private ItemMainType(int value, InventoryTabType tabType) {
        this.val = value;
        this.tabType = tabType;
    }
}
