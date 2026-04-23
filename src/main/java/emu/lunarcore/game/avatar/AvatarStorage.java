package emu.lunarcore.game.avatar;

import java.util.Iterator;
import java.util.stream.Stream;

import org.bson.types.ObjectId;

import emu.lunarcore.LunarCore;
import emu.lunarcore.data.GameData;
import emu.lunarcore.data.excel.AvatarExcel;
import emu.lunarcore.data.excel.MultiplePathAvatarExcel;
import emu.lunarcore.game.inventory.GameItem;
import emu.lunarcore.game.player.BasePlayerManager;
import emu.lunarcore.game.player.Player;
import emu.lunarcore.game.trial.TrialAvatar;
import emu.lunarcore.server.packet.send.PacketPlayerSyncScNotify;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import lombok.Getter;

@Getter
public class AvatarStorage extends BasePlayerManager implements Iterable<GameAvatar> {
    private final Int2ObjectMap<GameAvatar> avatars;
    private final Object2ObjectMap<ObjectId, GameAvatar> avatarObjectIdMap;
    
    private final Int2ObjectMap<AvatarMultiPath> multiPaths;
    private final Object2ObjectMap<ObjectId, AvatarMultiPath> multiPathsObjectIdMap;
    
    private final Int2ObjectMap<GameAvatar> tempAvatars;
    private final IntSet globalBuffAvatars;
    
    public AvatarStorage(Player player) {
        super(player);
        this.avatars = new Int2ObjectOpenHashMap<>();
        this.avatarObjectIdMap = new Object2ObjectOpenHashMap<>();
        this.multiPaths = new Int2ObjectOpenHashMap<>();
        this.multiPathsObjectIdMap = new Object2ObjectOpenHashMap<>();
        this.tempAvatars = new Int2ObjectOpenHashMap<>();
        this.globalBuffAvatars = new IntOpenHashSet();
    }

    public int getAvatarCount() {
        return this.avatars.size();
    }
    
    // Base avatars
    
    public BaseAvatar getBaseAvatarById(int id) {
        BaseAvatar baseAvatar = this.getMultiPathById(id);
        if (baseAvatar == null) {
            baseAvatar = this.getAvatarById(id);
        }
        
        return baseAvatar;
    }
    
    public BaseAvatar getBaseAvatarById(ObjectId id) {
        BaseAvatar baseAvatar = this.getMultiPathById(id);
        if (baseAvatar == null) {
            baseAvatar = this.getAvatarById(id);
        }
        
        return baseAvatar;
    }

    // Game avatars
    
    public GameAvatar getAvatarById(int id) {
        // Check if we are trying to get a temporary avatar
        if (this.tempAvatars.containsKey(id)) {
            return this.tempAvatars.get(id);
        }
        
        // Check if we are trying to retrieve a multi path character
        var multiPathExcel = GameData.getMultiplePathAvatarExcelMap().get(id);
        if (multiPathExcel != null) {
            id = multiPathExcel.getBaseAvatarID();
        }
        
        // Get from map
        return getAvatars().get(id);
    }
    
    public GameAvatar getAvatarById(ObjectId id) {
        if (id == null) {
            return null;
        }
        
        return getAvatarObjectIdMap().get(id);
    }

    public boolean hasAvatar(int id) {
        return getAvatarById(id) != null;
    }

    public boolean addAvatar(GameAvatar avatar) {
        // Sanity - Dont add avatars we already have OR avatars that dont have excel data
        if (avatar.getExcel() == null || this.hasAvatar(avatar.getAvatarId())) {
            return false;
        }
        
        // Check if avatar has multiple paths
        var pathExcel = GameData.getMultiplePathAvatarExcelMap().get(avatar.getAvatarId());
        if (pathExcel != null) {
            if (pathExcel.isDefault()) {
                // Apply path to avatar
                var path = this.getMultiPathById(avatar.getAvatarId());
                avatar.setMultiPath(path);
            } else {
                // Skip if not a default path
                return false;
            }
        }

        // Set owner first
        avatar.setOwner(getPlayer());
        
        // Save avatar
        avatar.save();

        // Put into avatar map
        this.avatars.put(avatar.getAvatarId(), avatar);
        this.avatarObjectIdMap.put(avatar.getId(), avatar);

        // Send packet
        getPlayer().sendPacket(new PacketPlayerSyncScNotify(avatar));
        
        // Add head icon
        getPlayer().getUnlocks().addHeadIcon(avatar.getHeadIconId());

        // Done
        return true;
    }
    
    public void addTempAvatar(TrialAvatar avatar) {
        this.tempAvatars.put(avatar.getSpecialAvatarExcel().getId(), avatar);
    }
    
    public void removeTempAvatar(int avatarId) {
        this.tempAvatars.remove(avatarId);
    }
    
    public AvatarMultiPath getMultiPathById(int id) {
        return getMultiPaths().get(id);
    }
    
    public AvatarMultiPath getMultiPathById(ObjectId id) {
        return getMultiPathsObjectIdMap().get(id);
    }
    
    /**
     * Updates hero types for players. Will create hero types if they dont exist already.
     */
    public void validateMultiPaths() {
        for (MultiplePathAvatarExcel pathExcel : GameData.getMultiplePathAvatarExcelMap().values()) {
            // Create path if it doesnt exist
            if (!getMultiPaths().containsKey(pathExcel.getId())) {
                // Get excel
                AvatarExcel excel = GameData.getAvatarExcelMap().get(pathExcel.getId());
                if (excel == null) continue;
                
                // Create path and save
                AvatarMultiPath path = new AvatarMultiPath(getPlayer(), excel);
                path.save();
                
                // Add
                getMultiPaths().put(path.getExcelId(), path);
                getMultiPathsObjectIdMap().put(path.getId(), path);
            }
            
            // Make sure we have a current avatar type
            if (pathExcel.isDefault() && this.getPlayer().getCurAvatarPathId(pathExcel.getBaseAvatarID()) == 0) {
                this.getPlayer().getCurAvatarPaths().put(pathExcel.getBaseAvatarID(), pathExcel.getAvatarID());
            }
        }
    }

    @Override
    public Iterator<GameAvatar> iterator() {
        return getAvatars().values().iterator();
    }

    // Database

    public void loadFromDatabase() {
        // Load multi paths first (Important)
        loadMultiPathsFromDatabase();
        
        // Load avatars
        Stream<GameAvatar> stream = LunarCore.getGameDatabase().getObjects(GameAvatar.class, "ownerUid", this.getPlayer().getUid());
        stream.forEach(this::loadAvatar);
    }
    
    private void loadMultiPathsFromDatabase() {
        // Get stream from database
        Stream<AvatarMultiPath> stream = LunarCore.getGameDatabase().getObjects(AvatarMultiPath.class, "ownerUid", this.getPlayer().getUid());

        stream.forEach(path -> {
            // Load avatar excel data
            AvatarExcel excel = GameData.getAvatarExcelMap().get(path.getExcelId());
            if (excel == null) {
                return;
            }
            
            path.setOwner(this.getPlayer());
            path.setExcel(excel);
            path.getData().fixSkills();
            
            // Add
            getMultiPaths().put(path.getExcelId(), path);
            getMultiPathsObjectIdMap().put(path.getId(), path);
        });
        
        // Setup hero paths if they dont exist
        this.validateMultiPaths();
    }
    
    public boolean loadAvatar(GameAvatar avatar) {
        // Should never happen
        if (avatar.getId() == null) {
            return false;
        }
        
        // Check avatar owner
        if (avatar.getOwnerUid() != this.getPlayer().getUid()) {
            return false;
        }
        
        // Set excel data
        if (avatar.hasMultiPath()) {
            // Get avatar's current path
            var path = getPlayer().getCurAvatarPath(avatar.getAvatarId());
            if (path == null) {
                return false;
            }
            
            avatar.setMultiPath(path);
        } else {
            // Load avatar excel data
            AvatarExcel excel = GameData.getAvatarExcelMap().get(avatar.getAvatarId());
            if (excel == null) {
                return false;
            }
            
            // Set ownerships
            avatar.setExcel(excel);
        }
        
        // Set ownership
        avatar.setOwner(getPlayer());
        
        // Fix skills
        avatar.getData().fixSkills();

        // Add to avatar storage
        this.avatars.put(avatar.getAvatarId(), avatar);
        this.avatarObjectIdMap.put(avatar.getId(), avatar);
        
        // Add to global buff avatar cache
        if (GameData.getAvatarGlobalBuffExcelMap().containsKey(avatar.getAvatarId())) {
            this.globalBuffAvatars.add(avatar.getAvatarId());
        }
        
        // Done
        return true;
    }
    
    public GameAvatar loadAvatarByObjectId(ObjectId id) {
        // Load hero paths first
        if (this.getMultiPaths().size() == 0) {
            this.loadMultiPathsFromDatabase();
        }
        
        // Load avatar
        GameAvatar avatar = LunarCore.getGameDatabase().getObjectByField(GameAvatar.class, "_id", id);
        
        if (this.loadAvatar(avatar)) {
            // Load items
            var stream = LunarCore.getGameDatabase().getObjects(GameItem.class, "equipAvatarId", id);
            stream.forEach(this.getPlayer().getInventory()::loadItem);
        }
        
        return avatar;
    }
}
