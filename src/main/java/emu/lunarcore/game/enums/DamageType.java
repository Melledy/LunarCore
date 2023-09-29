package emu.lunarcore.game.enums;

import lombok.Getter;

// These are in excels but i prefer them as enums
public enum DamageType {
    Physical    (1000111),
    Fire        (1000112),
    Ice         (1000113),     
    Thunder     (1000114), 
    Wind        (1000115), 
    Quantum     (1000116), 
    Imaginary   (1000117);
    
    @Getter
    private int enterBattleBuff;
    
    private DamageType(int enterBattleBuff) {
        this.enterBattleBuff = enterBattleBuff;
    }
}
