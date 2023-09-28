package emu.lunarcore.game.avatar;

import dev.morphia.annotations.Entity;
import lombok.Getter;
import lombok.Setter;

@Entity(useDiscriminator = false)
public class AvatarRank {
    @Getter @Setter
    private int value;
    
    /**
     * A helper class that contains information about an avatar's rank.
     */
    public AvatarRank() {
        
    }
}
