package emu.lunarcore.game.inventory.tabs;

import lombok.Getter;

@Getter
public enum InventoryTabType {
    NONE        (0),
    MATERIAL    (1), 
    EQUIPMENT   (2), 
    RELIC       (3);
    
    private int val;
    
    private InventoryTabType(int value) {
        this.val = value;
    }
}
