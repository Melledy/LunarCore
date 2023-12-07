package emu.lunarcore.game.gacha;

import dev.morphia.annotations.Entity;
import emu.lunarcore.GameConstants;
import emu.lunarcore.game.avatar.GameAvatar;
import emu.lunarcore.game.player.Player;
import emu.lunarcore.proto.GachaCeilingAvatarOuterClass.GachaCeilingAvatar;
import emu.lunarcore.proto.GachaCeilingOuterClass.GachaCeiling;
import lombok.Getter;
import lombok.Setter;

@Getter @Entity(useDiscriminator = false)
public class PlayerGachaInfo {
    private PlayerGachaBannerInfo standardBanner;
    private PlayerGachaBannerInfo eventCharacterBanner;
    private PlayerGachaBannerInfo eventWeaponBanner;
    
    private int ceilingNum;
    @Setter private boolean ceilingClaimed;

    public PlayerGachaInfo() {
        this.standardBanner = new PlayerGachaBannerInfo();
        this.eventCharacterBanner = new PlayerGachaBannerInfo();
        this.eventWeaponBanner = new PlayerGachaBannerInfo();
    }
    
    public void addCeilingNum(int amount) {
        this.ceilingNum = Math.min(ceilingNum + amount, GameConstants.GACHA_CEILING_MAX);
    }

    public PlayerGachaBannerInfo getBannerInfo(GachaType type) {
        if (type == GachaType.AvatarUp) {
            return this.eventCharacterBanner;
        } else if (type == GachaType.WeaponUp) {
            return this.eventWeaponBanner;
        }

        return this.standardBanner;
    }
    
    public GachaCeiling toGachaCeiling(Player player) {
        var proto = GachaCeiling.newInstance()
                .setIsClaimed(this.isCeilingClaimed())
                .setCeilingNum(this.getCeilingNum());
        
        // Gacha ceiling avatars are the avatars that we can pick
        var ceilingAvatars = player.getServer().getGachaService().getYellowAvatars();
        for (int i = 0; i < ceilingAvatars.length; i++) {
            int avatarId = ceilingAvatars[i];
            int repeatedCount = 0; // Eidolon count
            
            GameAvatar avatar = player.getAvatarById(avatarId);
            if (avatar != null) {
                repeatedCount = avatar.getRank();
            }
            
            var ceilingAvatar = GachaCeilingAvatar.newInstance()
                .setRepeatedCnt(repeatedCount)
                .setAvatarId(avatarId);
            
            proto.addAvatarList(ceilingAvatar);
        }
        
        return proto;
    }
}
