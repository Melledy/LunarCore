package emu.lunarcore.game.friends;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import dev.morphia.annotations.Indexed;
import emu.lunarcore.LunarCore;
import emu.lunarcore.game.player.Player;
import emu.lunarcore.proto.SimpleInfoOuterClass.SimpleInfo;
import lombok.Getter;
import lombok.Setter;

@Getter
@Entity(value = "friendships", useDiscriminator = false)
public class Friendship {
    @Id private long id;
    
    @Indexed private int ownerUid;
    @Indexed private int friendUid;
    
    @Setter private boolean isFriend;
    private int askerUid;
    
    @Setter private transient Player owner;
    @Setter private transient SimpleInfo simpleInfo;
    
    @Deprecated // Morphia use only
    public Friendship() { }
    
    public Friendship(Player owner, Player friend, Player asker) {
        this.owner = owner;
        this.id = Friendship.generateUniqueKey(owner.getUid(), this.getFriendUid());
        this.ownerUid = owner.getUid();
        this.friendUid = friend.getUid();
        this.askerUid = asker.getUid();
    }
    
    // Database functions
    
    public void save() {
        LunarCore.getGameDatabase().save(this);
    }

    public void delete() {
        LunarCore.getGameDatabase().delete(this);
    }
    
    // Extra
    
    /**
     * Creates an unique key for a friendship object using 2 player uids
     */
    public static long generateUniqueKey(int ownerUid, int targetUid) {
        return ((long) ownerUid << 32) + targetUid;
    }
}
