package emu.lunarcore.game.friends;

import emu.lunarcore.GameConstants;
import emu.lunarcore.LunarCore;
import emu.lunarcore.game.player.BasePlayerManager;
import emu.lunarcore.game.player.Player;
import emu.lunarcore.server.packet.BasePacket;
import emu.lunarcore.server.packet.send.*;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import lombok.AccessLevel;
import lombok.Getter;

@Getter(AccessLevel.PRIVATE)
public class FriendList extends BasePlayerManager {
    @Getter(AccessLevel.PUBLIC)
    private final Int2ObjectMap<Friendship> friends;
    @Getter(AccessLevel.PUBLIC)
    private final Int2ObjectMap<Friendship> pendingFriends;
    
    private long friendListCooldown = 0;
    private long applyFriendListCooldown = 0;
    private BasePacket friendListPacket;
    private BasePacket applyFriendListPacket;
    
    public FriendList(Player player) {
        super(player);
        this.friends = new Int2ObjectOpenHashMap<Friendship>();
        this.pendingFriends = new Int2ObjectOpenHashMap<Friendship>();
    }
    
    private synchronized Friendship getFriendById(int id) {
        if (this.getPlayer().isOnline()) {
            return this.getFriends().get(id);
        } else {
            return LunarCore.getGameDatabase().getObjectByUid(Friendship.class, Friendship.generateUniqueKey(getPlayer().getUid(), id));
        }
    }

    private synchronized Friendship getPendingFriendById(int id) {
        if (this.getPlayer().isOnline()) {
            return this.getPendingFriends().get(id);
        } else {
            return LunarCore.getGameDatabase().getObjectByUid(Friendship.class, Friendship.generateUniqueKey(getPlayer().getUid(), id));
        }
    }
    
    private void addFriendship(Friendship friendship) {
        getFriends().put(friendship.getFriendUid(), friendship);
        this.friendListCooldown = 0;
    }

    private void addPendingFriendship(Friendship friendship) {
        getPendingFriends().put(friendship.getFriendUid(), friendship);
        this.applyFriendListCooldown = 0;
    }
    
    private void removeFriendship(int uid) {
        getFriends().remove(uid);
        this.friendListCooldown = 0;
    }

    private void removePendingFriendship(int uid) {
        getPendingFriends().remove(uid);
        this.applyFriendListCooldown = 0;
    }
    
    /**
     * Gets total amount of potential friends
     */
    public int getFullFriendCount() {
        return this.getPendingFriends().size() + this.getFriends().size();
    }
    
    public synchronized void handleFriendRequest(int targetUid, boolean action) {
        // Make sure we have enough room
        if (this.getFriends().size() >= GameConstants.MAX_FRIENDSHIPS) {
            return;
        }
        
        // Check if player has sent friend request
        Friendship myFriendship = this.getPendingFriendById(targetUid);
        if (myFriendship == null) return;
        
        // Make sure this player is not the asker
        if (myFriendship.getAskerUid() == this.getPlayer().getUid()) return;
        
        // Get target player
        Player target = getServer().getPlayerByUid(targetUid, true);
        if (target == null) return;
        
        // Get target player's friendship
        Friendship theirFriendship = target.getFriendList().getPendingFriendById(getPlayer().getUid());
        
        if (theirFriendship == null) {
            // They dont have us on their friends list anymore, rip
            this.removePendingFriendship(myFriendship.getOwnerUid());
            myFriendship.delete();
            getPlayer().sendPacket(new PacketHandleFriendScRsp(target, false));
            return;
        }
        
        // Handle action
        if (action) {
            // Request accepted
            myFriendship.setFriend(true);
            theirFriendship.setFriend(true);

            this.removePendingFriendship(myFriendship.getOwnerUid());
            this.addFriendship(myFriendship);

            if (target.isOnline()) {
                target.getFriendList().removePendingFriendship(this.getPlayer().getUid());
                target.getFriendList().addFriendship(theirFriendship);
                target.sendPacket(new PacketSyncHandleFriendScNotify(getPlayer(), action));
            }

            // Save friendships to the database
            myFriendship.save();
            theirFriendship.save();
        } else {
            // Request declined - Delete from my pending friends
            this.removePendingFriendship(myFriendship.getOwnerUid());

            if (target.isOnline()) {
                target.getFriendList().removePendingFriendship(getPlayer().getUid());
                target.sendPacket(new PacketSyncHandleFriendScNotify(getPlayer(), action));
            }
            
            // Delete friendships from the database
            myFriendship.delete();
            theirFriendship.delete();
        }
        
        // Send packet
        getPlayer().sendPacket(new PacketHandleFriendScRsp(target, action));
    }
    
    public synchronized void sendFriendRequest(int targetUid) {
        // Get target and sanity check
        Player target = getPlayer().getServer().getPlayerByUid(targetUid, true);
        if (target == null || target == this.getPlayer()) return;
        
        // Check if friend already exists
        if (getPendingFriends().containsKey(targetUid) || getFriends().containsKey(targetUid)) {
            return;
        }
        
        // Create friendships
        Friendship myFriendship = new Friendship(getPlayer(), target, getPlayer());
        Friendship theirFriendship = new Friendship(target, getPlayer(), getPlayer());
        
        // Add to our pending friendship list
        this.addPendingFriendship(myFriendship);

        if (target.isOnline()) {
            target.getFriendList().addPendingFriendship(theirFriendship);
            target.sendPacket(new PacketSyncApplyFriendScNotify(this.getPlayer()));
        }
        
        // Save friendships to the database
        myFriendship.save();
        theirFriendship.save();
    }
    
    public synchronized void deleteFriend(int targetUid) {
        // Get friendship
        Friendship myFriendship = this.getFriendById(targetUid);
        if (myFriendship == null) return;

        // Remove from friends list
        this.removeFriendship(targetUid);
        myFriendship.delete();

        // Delete from friend's friend list
        Player friend = getServer().getPlayerByUid(targetUid, true);
        
        if (friend != null) {
            // Friend online
            Friendship theirFriendship = friend.getFriendList().getFriendById(this.getPlayer().getUid());
            
            if (theirFriendship != null) {
                // Delete friendship on friends side
                theirFriendship.delete();
                
                if (friend.isOnline()) {
                    // Remove from online friend's friend list
                    friend.getFriendList().removeFriendship(theirFriendship.getFriendUid());
                    
                    // Send packet to friend
                    getPlayer().sendPacket(new PacketSyncDeleteFriendScNotify(getPlayer().getUid()));
                }
            }
        }

        // Send packet
        getPlayer().sendPacket(new PacketSyncDeleteFriendScNotify(targetUid));
    }
    
    // Database
    
    public synchronized void loadFromDatabase() {
        var friendships = LunarCore.getGameDatabase().getObjects(Friendship.class, "ownerUid", this.getPlayer().getUid());
        
        friendships.forEach(friendship -> {
            // Set ownership first
            friendship.setOwner(getPlayer());
            
            // Finally, load to our friends list
            if (friendship.isFriend()) {
                getFriends().put(friendship.getFriendUid(), friendship);
            } else {
                getPendingFriends().put(friendship.getFriendUid(), friendship);
            }
        });
    }
    
    // Protobuf serialization
    
    public synchronized int[] toFriendUidArray() {
        IntArrayList list = new IntArrayList();
        
        list.add(GameConstants.SERVER_CONSOLE_UID);
        
        for (var friendship : this.getFriends().values()) {
            list.add(friendship.getFriendUid());
        }
        
        return list.toIntArray();
    }

    public synchronized BasePacket getFriendListPacket() {
        if (this.friendListPacket == null || System.currentTimeMillis() >= this.friendListCooldown) {
            this.friendListPacket = new PacketGetFriendListInfoScRsp(this);
            this.friendListCooldown = System.currentTimeMillis() + 60000;
        }
        return this.friendListPacket;
    }

    public synchronized BasePacket getApplyFriendListPacket() {
        if (this.applyFriendListPacket == null || System.currentTimeMillis() >= this.applyFriendListCooldown) {
            this.applyFriendListPacket = new PacketGetFriendApplyListInfoScRsp(this);
            this.applyFriendListCooldown = System.currentTimeMillis() + 60000;
        }
        return this.applyFriendListPacket;
    }
}
