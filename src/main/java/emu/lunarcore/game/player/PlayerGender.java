package emu.lunarcore.game.player;

import dev.morphia.annotations.Entity;
import lombok.Getter;

@Entity(useDiscriminator = false)
public enum PlayerGender {
    GENDER_NONE(0, "GenderNone"),
    GENDER_MAN(1, "GenderMan"),
    GENDER_WOMAN(2, "GenderWoman");

    @Getter
    private final int val;

    @Getter
    private final String altName;

    private PlayerGender(int val, String altName) {
        this.val = val;
        this.altName = altName;
    }

    // Method to get the enum constant by alternative name
    public static PlayerGender fromAltName(String altName) {
        for (PlayerGender gender : PlayerGender.values()) {
            if (gender.altName.equalsIgnoreCase(altName)) {
                return gender;
            }
        }
        throw new IllegalArgumentException("No enum constant with alternative name: " + altName);
    }
}