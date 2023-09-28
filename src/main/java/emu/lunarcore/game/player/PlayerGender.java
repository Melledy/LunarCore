package emu.lunarcore.game.player;

import dev.morphia.annotations.Entity;
import lombok.Getter;

@Entity(useDiscriminator = false)
public enum PlayerGender {
    GENDER_NONE     (0),
    GENDER_MAN      (1),
    GENDER_WOMAN    (2);
    
    @Getter
    private final int val;
    
    /** 
     * Official name: GenderType
     */
    private PlayerGender(int val) {
        this.val = val;
    }
}
