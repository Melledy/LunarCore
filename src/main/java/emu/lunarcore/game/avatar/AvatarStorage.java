package emu.lunarcore.game.avatar;

import java.util.Iterator;
import java.util.stream.Stream;

import emu.lunarcore.GameConstants;
import emu.lunarcore.LunarCore;
import emu.lunarcore.data.GameData;
import emu.lunarcore.data.excel.AvatarExcel;
import emu.lunarcore.data.excel.HeroExcel;
import emu.lunarcore.game.player.BasePlayerManager;
import emu.lunarcore.game.player.Player;
import emu.lunarcore.server.packet.send.PacketPlayerSyncScNotify;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import lombok.Getter;

@Getter
public class AvatarStorage extends BasePlayerManager implements Iterable<GameAvatar> {
    private final Int2ObjectMap<GameAvatar> avatars;
    private final Int2ObjectMap<AvatarHeroPath> heroPaths;
    
    public AvatarStorage(Player player) {
        super(player);
        this.avatars = new Int2ObjectOpenHashMap<>();
        this.heroPaths = new Int2ObjectOpenHashMap<>();
    }

    public int getAvatarCount() {
        return this.avatars.size();
    }

    public GameAvatar getAvatarById(int id) {
        return getAvatars().get(id);
    }

    public boolean hasAvatar(int id) {
        return getAvatars().containsKey(id);
    }

    public boolean addAvatar(GameAvatar avatar) {
        // Sanity - Dont add avatars we already have OR avatars that dont have excel data
        if (avatar.getExcel() == null || this.hasAvatar(avatar.getAvatarId())) {
            return false;
        }
        
        // Dont add more than 1 main character
        if (avatar.isHero() && this.hasAvatar(GameConstants.TRAILBLAZER_AVATAR_ID)) {
            return false;
        }

        // Set owner first
        avatar.setOwner(getPlayer());

        // Put into avatar map
        this.avatars.put(avatar.getAvatarId(), avatar);

        // Save to database and send packet
        avatar.save();
        getPlayer().sendPacket(new PacketPlayerSyncScNotify(avatar));
        
        // Add head icon
        getPlayer().getUnlocks().addHeadIcon(avatar.getHeadIconId());

        // Done
        return true;
    }
    
    public AvatarHeroPath getHeroPathById(int id) {
        return getHeroPaths().get(id);
    }
    
    /**
     * Updates hero types for players. Will create hero types if they dont exist already.
     */
    public void validateHeroPaths() {
        for (HeroExcel heroExcel : GameData.getHeroExcelMap().values()) {
            if (getHeroPaths().containsKey(heroExcel.getId())) continue;
            
            AvatarExcel excel = GameData.getAvatarExcelMap().get(heroExcel.getId());
            if (excel == null) continue;
            
            AvatarHeroPath path = new AvatarHeroPath(getPlayer(), excel);
            path.save();
            getHeroPaths().put(path.getId(), path);
        }
    }

    @Override
    public Iterator<GameAvatar> iterator() {
        return getAvatars().values().iterator();
    }

    // Database

    public void loadFromDatabase() {
        // Load hero paths
        Stream<AvatarHeroPath> heroStream = LunarCore.getGameDatabase().getObjects(AvatarHeroPath.class, "ownerUid", this.getPlayer().getUid());

        heroStream.forEach(heroPath -> {
            // Load avatar excel data
            AvatarExcel excel = GameData.getAvatarExcelMap().get(heroPath.getId());
            if (excel == null) {
                return;
            }
            
            heroPath.setExcel(excel);
            
            this.heroPaths.put(heroPath.getId(), heroPath);
        });
        
        // Setup hero paths if they dont exist
        this.validateHeroPaths();
        
        // Load avatars
        Stream<GameAvatar> stream = LunarCore.getGameDatabase().getObjects(GameAvatar.class, "ownerUid", this.getPlayer().getUid());

        stream.forEach(avatar -> {
            // Should never happen
            if (avatar.getId() == null) {
                return;
            }
            
            // Set hero path
            if (avatar.isHero()) {
                avatar.setHeroPath(getPlayer().getCurHeroPath());
            } else {
                // Load avatar excel data
                AvatarExcel excel = GameData.getAvatarExcelMap().get(avatar.getAvatarId());
                if (excel == null) {
                    return;
                }
                
                // Set ownerships
                avatar.setExcel(excel);
            }
            
            // Set ownership
            avatar.setOwner(getPlayer());

            // Add to avatar storage
            this.avatars.put(avatar.getAvatarId(), avatar);
        });
    }
}
