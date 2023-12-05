package emu.lunarcore.game.inventory.tabs;

import lombok.Getter;

@Getter
public enum InventoryTabType {
    MATERIAL    (0), 
    EQUIPMENT   (1), 
    RELIC       (2);
    
    private int val;
    
    private InventoryTabType(int value) {
        this.val = value;
    }
}
