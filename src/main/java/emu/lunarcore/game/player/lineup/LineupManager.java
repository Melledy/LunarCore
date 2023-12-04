package emu.lunarcore.game.player.lineup;

import java.util.List;

import dev.morphia.annotations.Entity;
import emu.lunarcore.GameConstants;
import emu.lunarcore.LunarCore;
import emu.lunarcore.game.avatar.GameAvatar;
import emu.lunarcore.game.player.Player;
import emu.lunarcore.proto.ExtraLineupTypeOuterClass.ExtraLineupType;
import emu.lunarcore.server.packet.send.PacketSyncLineupNotify;

import lombok.Getter;

@Entity(useDiscriminator = false) @Getter
public class LineupManager {
    private transient Player player;
    
    private int currentIndex; // Team index
    private int mp;
    
    private transient int currentExtraIndex;
    
    private transient PlayerLineup[] lineups;
    private transient PlayerExtraLineup[] extraLineups;

    @Deprecated // Morphia only!
    public LineupManager() {
        this.lineups = new PlayerLineup[GameConstants.DEFAULT_TEAMS];
        this.extraLineups = new PlayerExtraLineup[ExtraLineupType.values().length];
    }

    public LineupManager(Player player) {
        this();
        this.player = player;
        this.mp = 5;
        
        this.validate();
    }
    
    public void setPlayer(Player player) {
        this.player = player;
    }
    
    protected void addMp(int i) {
        this.mp = Math.min(this.mp + i, GameConstants.MAX_MP);
        this.getPlayer().sendPacket(new PacketSyncLineupNotify(player.getCurrentLineup()));
    }
    
    protected void setMp(int i) {
        this.mp = i;
    }
    
    protected void removeMp(int i) {
        this.mp = Math.max(this.mp - i, 0);
    }
    
    /**
     * Sets the player's current extra lineup type.
     * @param type Extra lineup type
     * @param sync Whether or not to sync lineup with scene. Not needed when changing scenes.
     */
    public void setCurrentExtraLineup(int type, boolean sync) {
        this.currentExtraIndex = type;
        this.getPlayer().save();

        if (sync) {
            // Sync with scene entities
            this.getPlayer().getScene().syncLineup();
            // Sync lineup data with client
            player.sendPacket(new PacketSyncLineupNotify(this.getExtraLineupByType(type)));
        }
    }
    
    /**
     * Sets the player's current extra lineup type.
     * @param type Extra lineup type
     * @param sync Whether or not to sync lineup with scene. Not needed when changing scenes.
     */
    public void setCurrentExtraLineup(ExtraLineupType type, boolean sync) {
        this.setCurrentExtraLineup(type.getNumber(), sync);
    }
    
    /**
     * Returns a lineup by the type
     * @param index Regular lineup index
     * @param extraLineup
     * @return
     */
    public PlayerLineup getLineupByIndex(int index, int extraLineupType) {
        // Sanity
        if (extraLineupType > 0) {
            return getExtraLineupByType(extraLineupType);
        } else {
            return getLineupByIndex(index);
        }
    }
    
    /**
     * Gets a regular player lineup by index. Only normal lineups are returned
     */
    public PlayerLineup getLineupByIndex(int index) {
        // Sanity check
        if (index < 0 || index >= this.lineups.length) {
            return null;
        }
        
        return this.lineups[index];
    }
    
    /**
     * Returns a lineup by ExtraLineupType. Creates a lineup for the player if it doesnt exist.
     * @param type ExtraLineupType
     */
    public PlayerLineup getExtraLineupByType(int type) {
        // Sanity check to make sure the extra lineup type actually exists
        if (type <= 0 || type >= this.extraLineups.length) {
            return null;
        }
        
        // Actually get the lineup
        PlayerExtraLineup lineup = this.extraLineups[type];
        
        if (lineup == null) {
            lineup = new PlayerExtraLineup(this.getPlayer(), type);
            this.extraLineups[type] = lineup;
        }
        
        return lineup;
    }
    
    /**
     * Returns the current lineup that the player is using.
     */
    public PlayerLineup getCurrentLineup() {
        return this.getLineupByIndex(this.currentIndex, this.currentExtraIndex);
    }
    
    /**
     * Returns the avatar that the player is playing as from the current lineup.
     */
    public GameAvatar getCurrentLeaderAvatar() {
        try {
            PlayerLineup lineup = this.getCurrentLineup();
            int avatarId = lineup.getAvatars().get(lineup.getLeader());
            return this.getPlayer().getAvatarById(avatarId);
        } catch (Exception e) {
            return null;
        }
    }

    // Lineup functions

    /**
     * Changes the player's current active avatar
     * @param slot The slot of the avatar we are changing to
     * @return true on success
     */
    public boolean changeLeader(int slot) {
        PlayerLineup lineup = this.getCurrentLineup();
        
        if (slot >= 0 && slot < lineup.size()) {
            lineup.setLeader(slot);
            return true;
        }

        return false;
    }

    /**
     * Adds an avatar to a lineup
     * @param index Index of the lineup we are adding the avatar to
     * @param slot The slot that we want to put the avatar at
     * @param avatarId Id of the avatar we are adding
     * @return true on success
     */
    public boolean joinLineup(int index, int slot, int avatarId) {
        // Get lineup
        PlayerLineup lineup = this.getLineupByIndex(index);
        if (lineup == null) return false;

        boolean isCurrentLineup = lineup == getCurrentLineup();

        // Get avatar
        GameAvatar avatar = getPlayer().getAvatarById(avatarId);
        if (avatar == null) return false;

        // Join lineup
        if (lineup.isActiveSlot(slot)) {
            // Replace avatar
            lineup.getAvatars().set(slot, avatarId);
        } else if (lineup.size() < GameConstants.MAX_AVATARS_IN_TEAM) {
            // Add avatar
            lineup.getAvatars().add(avatarId);
        } else {
            // No changes were made
            return false;
        }
        
        // Save
        lineup.save();

        // Sync lineup with scene
        if (isCurrentLineup) {
            player.getScene().syncLineup();
        }

        // Sync lineup data
        player.sendPacket(new PacketSyncLineupNotify(lineup));

        return true;
    }

    /**
     * Removes an avatar from a lineup
     * @param index Index of the lineup we are removing the avatar from
     * @param slot The slot that we want to remove the avatar from
     * @param avatarId Id of the avatar we are removing
     * @return true on success
     */
    public boolean quitLineup(int index, int avatarId) {
        // Get lineup
        PlayerLineup lineup = this.getLineupByIndex(index);
        if (lineup == null) return false;

        boolean isCurrentLineup = lineup == getCurrentLineup();

        // Sanity check to make sure were not removing the last member of our lineup
        if (isCurrentLineup && lineup.size() <= 1) {
            return false;
        }

        // Remove avatar from lineup
        int i = lineup.getAvatars().indexOf(avatarId);
        if (i != -1) {
            lineup.getAvatars().remove(i);
        } else {
            return false;
        }

        // Validate leader slot
        if (lineup.getLeader() >= lineup.size()) {
            lineup.setLeader(0);
        }

        // Save
        lineup.save();

        // Sync lineup with scene
        if (isCurrentLineup) {
            player.getScene().syncLineup();
        }

        // Sync lineup data
        player.sendPacket(new PacketSyncLineupNotify(lineup));

        return true;
    }

    /**
     * Changes the player's active lineup
     * @param index Index of the lineup we are changing to
     * @return true on success
     */
    public boolean switchLineup(int index) {
        // Sanity + Prevent lineups from being changed when the player is using an extra lineup
        if (index == this.getCurrentIndex() || this.currentExtraIndex > 0) {
            return false;
        }

        // Get lineup
        PlayerLineup lineup = this.getLineupByIndex(index);

        // Make sure our next lineup exists and has avatars in it
        if (lineup == null || lineup.size() == 0) {
            return false;
        }

        // Set index
        this.currentIndex = index;

        // Save player
        this.getPlayer().save();

        // Sync lineup data
        player.getScene().syncLineup();
        player.sendPacket(new PacketSyncLineupNotify(lineup));

        return true;
    }

    /**
     * Sets the avatars of a lineup
     * @param index Index of the lineup we are replacing the avatars on
     * @param extraLineupType
     * @param lineupList New avatar list
     * @return true on success
     */
    public boolean replaceLineup(int index, int extraLineupType, List<Integer> lineupList) {
        // Get lineup
        PlayerLineup lineup = this.getLineupByIndex(index, extraLineupType);
        if (lineup == null) return false;
        
        // Sanity - Make sure player cant remove all avatars from the current lineup
        if (lineup == this.getCurrentLineup() && lineupList.size() == 0) {
            return false;
        }

        // Clear
        lineup.getAvatars().clear();

        // Add
        for (int avatarId : lineupList) {
            GameAvatar avatar = getPlayer().getAvatarById(avatarId);
            if (avatar != null) {
                lineup.getAvatars().add(avatarId);
            }
        }

        // Validate leader slot
        if (lineup.getLeader() >= lineup.size()) {
            lineup.setLeader(0);
        }

        // Save
        lineup.save();

        // Sync lineup with scene
        if (lineup == getCurrentLineup()) {
            player.getScene().syncLineup();
        }

        // Sync lineup data
        player.sendPacket(new PacketSyncLineupNotify(lineup));

        return true;
    }

    /**
     * Swaps the positions of 2 avatars on a lineup
     * @param index Index of the lineup we are swapping avatars on
     * @param src 1st avatar slot
     * @param dest 2nd avatar slot
     * @return true on success
     */
    public boolean swapLineup(int index, int src, int dest) {
        // Sanity
        if (src == dest) return false;

        // Get lineup
        PlayerLineup lineup = this.getLineupByIndex(index);
        if (lineup == null) return false;
        
        // Validate slots to make sure avatars are in them
        if (!lineup.isActiveSlot(src) || !lineup.isActiveSlot(dest)) {
            return false;
        }

        // Swap
        int srcId = lineup.getAvatars().get(src);
        int destId = lineup.getAvatars().get(dest);

        lineup.getAvatars().set(src, destId);
        lineup.getAvatars().set(dest, srcId);

        // Save
        lineup.save();

        // Sync lineup data
        player.sendPacket(new PacketSyncLineupNotify(lineup));

        return true;
    }

    /**
     * Changes a lineup's name
     * @param index
     * @param name
     * @return
     */
    public boolean changeLineupName(int index, String name) {
        // Get lineup
        PlayerLineup lineup = this.getLineupByIndex(index);
        if (lineup == null) return false;

        // Change name and save lineup
        lineup.setName(name);
        lineup.save();
        
        return true;
    }
    
    // Database
    
    public void loadFromDatabase() {
        // Load lineups from database
        var list = LunarCore.getGameDatabase()
                .getObjects(PlayerLineup.class, "ownerUid", getPlayer().getUid())
                .toList();
        
        for (var lineup : list) {
            // Set owner
            lineup.setOwner(this.getPlayer());
            
            // Add to lineups
            try {
                this.lineups[lineup.getIndex()] = lineup; 
            } catch (Exception e) {
                lineup.delete();
            }
        }
        
        // Load extra lineups from database
        var extraList = LunarCore.getGameDatabase()
                .getObjects(PlayerExtraLineup.class, "ownerUid", getPlayer().getUid())
                .toList();
        
        for (var lineup : extraList) {
            // Set owner
            lineup.setOwner(this.getPlayer());
            
            // Add to lineups
            try {
                this.extraLineups[lineup.getExtraLineupType()] = lineup; 
            } catch (Exception e) {
                lineup.delete();
            }
        }
        
        // Validate lineups
        this.validate();
    }
    
    private void validate() {
        // Populate all lineups
        for (int i = 0; i < GameConstants.DEFAULT_TEAMS; i++) {
            PlayerLineup lineup = this.lineups[i];
            
            if (lineup == null) {
                lineup = new PlayerLineup(this.getPlayer(), i);
                this.lineups[i] = lineup;
            } else {
                lineup.setOwner(this.getPlayer());
            }
        }
        
        // Make sure current lineup has at least one avatar
        PlayerLineup lineup = this.getCurrentLineup();
        if (lineup == null) {
            this.currentIndex = 0;
            this.currentExtraIndex = 0;
            lineup = this.getCurrentLineup();
        }
        
        if (lineup.getAvatars().size() == 0) {
            GameAvatar avatar = this.getPlayer().getAvatarById(GameConstants.TRAILBLAZER_AVATAR_ID);
            if (avatar != null) {
                lineup.getAvatars().add(avatar.getAvatarId());
            }
        }
    }
}
