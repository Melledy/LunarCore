package emu.lunarcore.game.player;

import java.util.ArrayList;
import java.util.List;

import dev.morphia.annotations.Entity;
import emu.lunarcore.GameConstants;
import emu.lunarcore.game.avatar.GameAvatar;
import emu.lunarcore.proto.ExtraLineupTypeOuterClass.ExtraLineupType;
import emu.lunarcore.proto.LineupInfoOuterClass.LineupInfo;
import emu.lunarcore.server.packet.send.PacketSyncLineupNotify;
import lombok.Getter;
import lombok.Synchronized;

@Entity(useDiscriminator = false) @Getter
public class PlayerLineup {
    private transient Player owner;
    private transient int index;
    private transient int extraLineupType;

    private String name;
    private List<Integer> avatars;

    @Deprecated // Morphia only!
    public PlayerLineup() {

    }
    
    public PlayerLineup(int index) {
        this(index, 0);
    }

    public PlayerLineup(int index, int extraLineupType) {
        this.extraLineupType = extraLineupType;
        this.avatars = new ArrayList<>(GameConstants.MAX_AVATARS_IN_TEAM);
        
        // Set team name if not an extra lineup
        if (!this.isExtraLineup()) {
            this.name = "Team " + (index + 1);
        } else {
            this.name = "";
        }
    }

    protected void setOwnerAndIndex(Player player, int index) {
        this.owner = player;
        this.index = index;
    }
    
    public boolean isExtraLineup() {
        return this.extraLineupType != 0;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Synchronized
    public List<Integer> getAvatars() {
        return avatars;
    }

    public int size() {
        return getAvatars().size();
    }
    
    public void heal(int heal) {
        // Flag to set if at least one avatar in the team has been healed
        boolean hasHealed = false;
        
        // Add hp to team
        for (int avatarId : this.getAvatars()) {
            GameAvatar avatar = this.getOwner().getAvatarById(avatarId);
            if (avatar == null) continue;
            
            if (avatar.getCurrentHp() < 10000) {
                avatar.setCurrentHp(Math.min(avatar.getCurrentHp() + heal, 10000));
                avatar.save();
                hasHealed = true;
            }
        }
        
        // Send packet if team was healed
        if (hasHealed) {
            getOwner().sendPacket(new PacketSyncLineupNotify(this));
        }
    }

    public LineupInfo toProto() {
        var proto = LineupInfo.newInstance()
                .setIndex(index)
                .setName(this.getName())
                .setLeaderSlot(this.getOwner().getLineupManager().getCurrentLeader())
                .setMp(this.getOwner().getLineupManager().getMp())
                .setMaxMp(GameConstants.MAX_MP)
                .setExtraLineupType(ExtraLineupType.LINEUP_NONE);

        for (int slot = 0; slot < this.getAvatars().size(); slot++) {
            GameAvatar avatar = owner.getAvatars().getAvatarById(getAvatars().get(slot));
            if (avatar == null) continue;

            proto.addAvatarList(avatar.toLineupAvatarProto(slot));
        }

        return proto;
    }
}
