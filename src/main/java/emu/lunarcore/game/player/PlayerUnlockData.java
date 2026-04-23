package emu.lunarcore.game.player;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import emu.lunarcore.GameConstants;
import emu.lunarcore.LunarCore;
import emu.lunarcore.data.GameData;
import emu.lunarcore.game.avatar.GameAvatar;
import emu.lunarcore.game.enums.PersonalizeShowType;
import emu.lunarcore.proto.PlayerSyncScNotifyOuterClass.PlayerSyncScNotify;
import emu.lunarcore.server.game.Syncable;
import emu.lunarcore.server.packet.BasePacket;
import emu.lunarcore.server.packet.send.PacketPlayerSyncScNotify;
import emu.lunarcore.server.packet.send.PacketUnlockAvatarSkinScNotify;
import emu.lunarcore.server.packet.send.PacketUnlockChatBubbleScNotify;
import emu.lunarcore.server.packet.send.PacketUnlockPhoneCaseScNotify;
import emu.lunarcore.server.packet.send.PacketUnlockPhoneThemeScNotify;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import lombok.Getter;

@Getter
@Entity(value = "unlocks", useDiscriminator = false)
public class PlayerUnlockData implements Syncable {
    private transient Player owner;
    
    @Id private int ownerUid;
    
    private IntSet headIcons;
    private IntSet headIconFrames;
    private IntSet chatBubbles;
    private IntSet phoneThemes;
    private IntSet phoneCases;
    private IntSet avatarSkins;
    private IntSet playerOutfits;
    private IntSet pets;
    
    @Deprecated // Morphia only
    public PlayerUnlockData() {}

    public PlayerUnlockData(Player player) {
        this.owner = player;
        this.ownerUid = player.getUid();
        
        // Add default head icons
        for (int iconId : GameConstants.DEFAULT_HEAD_ICONS) {
            this.addHeadIcon(iconId);
        }
        
        // Add head icons from avatars we already have
        for (GameAvatar avatar : owner.getAvatars()) {
            this.addHeadIcon(avatar.getHeadIconId());
        }
        
        // Add default chat bubble(s)
        for (var excel : GameData.getChatBubbleExcelMap().values()) {
            if (excel.getShowType() == PersonalizeShowType.Always) {
                this.addChatBubble(excel.getId());
            }
        }
        
        // Add default phone theme(s)
        for (var excel : GameData.getPhoneThemeExcelMap().values()) {
            if (excel.getShowType() == PersonalizeShowType.Always) {
                this.addPhoneTheme(excel.getId());
            }
        }
        
        this.save();
    }
    
    protected void setOwner(Player player) {
        this.owner = player;
    }
    
    public IntSet getHeadIcons() {
        if (this.headIcons == null) {
            this.headIcons = new IntOpenHashSet();
        }
        return this.headIcons;
    }
    
    public IntSet getHeadIconFrames() {
        if (this.headIconFrames == null) {
            this.headIconFrames = new IntOpenHashSet();
        }
        return this.headIconFrames;
    }
    
    public IntSet getChatBubbles() {
        if (this.chatBubbles == null) {
            this.chatBubbles = new IntOpenHashSet();
        }
        return this.chatBubbles;
    }
    
    public IntSet getPhoneThemes() {
        if (this.phoneThemes == null) {
            this.phoneThemes = new IntOpenHashSet();
        }
        return this.phoneThemes;
    }
    
    public IntSet getPhoneCases() {
        if (this.phoneCases == null) {
            this.phoneCases = new IntOpenHashSet();
        }
        return this.phoneCases;
    }
    
    public IntSet getAvatarSkins() {
        if (this.avatarSkins == null) {
            this.avatarSkins = new IntOpenHashSet();
        }
        return this.avatarSkins;
    }
    
    public IntSet getPlayerOutfits() {
        if (this.playerOutfits == null) {
            this.playerOutfits = new IntOpenHashSet();
        }
        return this.playerOutfits;
    }
    
    public IntSet getPets() {
        if (this.pets == null) {
            this.pets = new IntOpenHashSet();
        }
        return this.pets;
    }
    
    public void addHeadIcon(int headIconId) {
        boolean success = this.getHeadIcons().add(headIconId);
        
        if (success && this.getOwner().isLoggedIn()) {
            this.sendPacket(new PacketPlayerSyncScNotify(this));
            LunarCore.getGameDatabase().addToSet(this, this.getOwnerUid(), "headIcons", headIconId);
        }
    }
    
    public void addHeadIconFrame(int headIconFrameId) {
        boolean success = this.getHeadIconFrames().add(headIconFrameId);
        
        if (success && this.getOwner().isLoggedIn()) {
            this.sendPacket(new PacketPlayerSyncScNotify(this));
            LunarCore.getGameDatabase().addToSet(this, this.getOwnerUid(), "headIconFrames", headIconFrameId);
        }
    }
    
    public void addChatBubble(int chatBubbleId) {
        boolean success = this.getChatBubbles().add(chatBubbleId);
        
        if (success && this.getOwner().isLoggedIn()) {
            this.sendPacket(new PacketUnlockChatBubbleScNotify(chatBubbleId));
            LunarCore.getGameDatabase().addToSet(this, this.getOwnerUid(), "chatBubbles", chatBubbleId);
        }
    }
    
    public void addPhoneTheme(int phoneThemeId) {
        boolean success = this.getPhoneThemes().add(phoneThemeId);
        
        if (success && this.getOwner().isLoggedIn()) {
            this.sendPacket(new PacketUnlockPhoneThemeScNotify(phoneThemeId));
            LunarCore.getGameDatabase().addToSet(this, this.getOwnerUid(), "phoneThemes", phoneThemeId);
        }
    }
    
    public void addPhoneCase(int phoneCaseId) {
        boolean success = this.getPhoneCases().add(phoneCaseId);
        
        if (success && this.getOwner().isLoggedIn()) {
            this.sendPacket(new PacketUnlockPhoneCaseScNotify(phoneCaseId));
            LunarCore.getGameDatabase().addToSet(this, this.getOwnerUid(), "phoneCases", phoneCaseId);
        }
    }
    
    public void addAvatarSkin(int avatarSkinId) {
        boolean success = this.getAvatarSkins().add(avatarSkinId);
        
        if (success && this.getOwner().isLoggedIn()) {
            this.sendPacket(new PacketUnlockAvatarSkinScNotify(avatarSkinId));
            LunarCore.getGameDatabase().addToSet(this, this.getOwnerUid(), "avatarSkins", avatarSkinId);
        }
    }
    
    public void addPlayerOutfit(int playerOutfitId) {
        boolean success = this.getPlayerOutfits().add(playerOutfitId);
        
        if (success && this.getOwner().isLoggedIn()) {
            // TODO packet
            LunarCore.getGameDatabase().addToSet(this, this.getOwnerUid(), "playerOutfits", playerOutfitId);
        }
    }
    
    public void addPet(int petItemId) {
        // Get pet excel TODO optimize
        var excel = GameData.getPetExcelMap().values().stream()
                .filter(e -> e.getPetItemID() == petItemId)
                .findFirst()
                .orElse(null);
        
        if (excel == null) {
            return;
        }
        
        // Add
        boolean success = this.getPets().add(excel.getPetID());
        
        if (success && this.getOwner().isLoggedIn()) {
            // Pet sync packet TODO
            LunarCore.getGameDatabase().addToSet(this, this.getOwnerUid(), "pets", excel.getPetID());
        }
    }
    
    private void sendPacket(BasePacket packet) {
        this.getOwner().sendPacket(packet);
    }
    
    // Player sync
    
    public void onSync(PlayerSyncScNotify proto) {
        proto.setBoardDataSync(this.getOwner().toBoardData());
    }
    
    // Database

    public void save() {
        LunarCore.getGameDatabase().save(this);
    }
}
