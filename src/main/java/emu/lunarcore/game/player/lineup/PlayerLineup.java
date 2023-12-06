package emu.lunarcore.game.player.lineup;

import java.util.List;
import java.util.function.Consumer;

import org.bson.types.ObjectId;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import dev.morphia.annotations.Indexed;
import emu.lunarcore.GameConstants;
import emu.lunarcore.LunarCore;
import emu.lunarcore.game.avatar.GameAvatar;
import emu.lunarcore.game.player.Player;
import emu.lunarcore.proto.LineupInfoOuterClass.LineupInfo;
import emu.lunarcore.server.packet.send.PacketSyncLineupNotify;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import lombok.Getter;
import lombok.Setter;

@Entity(value = "lineups", useDiscriminator = false) @Getter
public class PlayerLineup {
    @Id private ObjectId id;
    
    @Indexed private int ownerUid;
    private transient Player owner;
    
    protected int index;
    protected IntList avatars;
    
    @Setter private int leader;
    @Setter private String name;

    @Deprecated // Morphia only!
    public PlayerLineup() {}
    
    public PlayerLineup(Player player, int index) {
        this.owner = player;
        this.ownerUid = player.getUid();
        this.index = index;
        this.avatars = new IntArrayList(GameConstants.MAX_AVATARS_IN_TEAM);
        
        // Set team name if not an extra lineup
        if (!this.isExtraLineup()) {
            this.name = "Team " + (index + 1);
        }
    }

    protected void setOwner(Player player) {
        this.owner = player;
    }
    
    public boolean isExtraLineup() {
        return false;
    }
    
    public int getIndex() {
        return this.index;
    }
    
    public int getExtraLineupType() {
        return 0;
    }

    public synchronized List<Integer> getAvatars() {
        return avatars;
    }
    
    public int size() {
        return getAvatars().size();
    }
    
    public void addMp(int i) {
        this.getOwner().getLineupManager().addMp(i);
    }
    
    public void setMp(int i) {
        this.getOwner().getLineupManager().setMp(i);
    }
    
    public void removeMp(int i) {
        this.getOwner().getLineupManager().removeMp(i);
    }
    
    public int getMp() {
        return this.getOwner().getLineupManager().getMp();
    }
    
    public void heal(int heal, boolean allowRevive) {
        // Flag to set if at least one avatar in the team has been healed
        boolean hasHealed = false;
        
        // Add hp to team
        for (int avatarId : this.getAvatars()) {
            GameAvatar avatar = this.getOwner().getAvatarById(avatarId);
            if (avatar == null) continue;
            
            // Dont heal dead avatars if we are not allowed to revive
            if (avatar.getCurrentHp(this) <= 0 && !allowRevive) {
                continue;
            }
            
            // Heal avatar
            if (avatar.getCurrentHp(this) < 10000) {
                avatar.setCurrentHp(this, Math.min(avatar.getCurrentHp(this) + heal, 10000));
                avatar.save();
                hasHealed = true;
            }
        }
        
        // Send packet if team was healed
        if (hasHealed) {
            getOwner().sendPacket(new PacketSyncLineupNotify(this));
        }
    }

    public void refreshLineup() {
        this.getOwner().sendPacket(new PacketSyncLineupNotify(this));
    }
    
    public void forEachAvatar(Consumer<GameAvatar> consumer) {
        for (int avatarId : this.getAvatars()) {
            GameAvatar avatar = this.getOwner().getAvatarById(avatarId);
            if (avatar == null) continue;
            
            consumer.accept(avatar);
        }
    }
    
    public int indexOf(int ownerId) {
        return this.getAvatars().indexOf(ownerId);
    }
    
    /**
     * Checks if the slot contains an avatar
     * @param slot The slot we are checking for
     * @return true if the slot contains an avatar
     */
    public synchronized boolean isActiveSlot(int slot) {
        return slot >= 0 && slot < this.size();
    }
    
    // Database
    
    public void save() {
        LunarCore.getGameDatabase().save(this);
    }
    
    public void delete() {
        LunarCore.getGameDatabase().delete(this);
    }
    
    // Serialization

    public LineupInfo toProto() {
        var proto = LineupInfo.newInstance()
                .setIndex(this.getIndex())
                .setLeaderSlot(this.getLeader())
                .setMp(this.getMp())
                .setMaxMp(GameConstants.MAX_MP)
                .setExtraLineupTypeValue(this.getExtraLineupType());
        
        if (this.getName() != null) {
            proto.setName(this.getName());
        }

        for (int slot = 0; slot < this.getAvatars().size(); slot++) {
            GameAvatar avatar = owner.getAvatars().getAvatarById(getAvatars().get(slot));
            if (avatar == null) continue;

            proto.addAvatarList(avatar.toLineupAvatarProto(this, slot));
        }

        return proto;
    }
}
