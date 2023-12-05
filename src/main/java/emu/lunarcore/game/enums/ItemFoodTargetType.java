package emu.lunarcore.game.enums;

import lombok.Getter;

@Getter
public enum ItemFoodTargetType {
    All (0),
    Alive (101),
    Dead (102);
    
    private int val;

    private ItemFoodTargetType(int value) {
        this.val = value;
    }
}
