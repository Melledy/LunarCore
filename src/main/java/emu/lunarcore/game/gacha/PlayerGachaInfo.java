package emu.lunarcore.game.gacha;

import dev.morphia.annotations.Entity;

@Entity(useDiscriminator = false)
public class PlayerGachaInfo {
    private PlayerGachaBannerInfo standardBanner;
    private PlayerGachaBannerInfo eventCharacterBanner;
    private PlayerGachaBannerInfo eventWeaponBanner;

    public PlayerGachaInfo() {
        this.standardBanner = new PlayerGachaBannerInfo();
        this.eventCharacterBanner = new PlayerGachaBannerInfo();
        this.eventWeaponBanner = new PlayerGachaBannerInfo();
    }

    public PlayerGachaBannerInfo getStandardBanner() {
        return standardBanner;
    }

    public PlayerGachaBannerInfo getEventCharacterBanner() {
        return eventCharacterBanner;
    }

    public PlayerGachaBannerInfo getEventWeaponBanner() {
        return eventWeaponBanner;
    }

    public PlayerGachaBannerInfo getBannerInfo(GachaType type) {
        if (type == GachaType.AvatarUp) {
            return this.eventCharacterBanner;
        } else if (type == GachaType.WeaponUp) {
            return this.eventWeaponBanner;
        }

        return this.standardBanner;
    }
}
