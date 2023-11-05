package emu.lunarcore.game.challenge;

import org.bson.types.ObjectId;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import dev.morphia.annotations.Indexed;
import emu.lunarcore.LunarCore;
import emu.lunarcore.game.player.Player;
import emu.lunarcore.proto.ChallengeRewardOuterClass.ChallengeReward;
import lombok.Getter;

@Getter
@Entity(value = "challengeReward", useDiscriminator = false)
public class ChallengeGroupReward {
    @Id
    private ObjectId id;
    
    @Indexed
    private int ownerUid;
    
    private int groupId;
    private long takenStars;
    
    @Deprecated // Morphia
    public ChallengeGroupReward() {}
    
    public ChallengeGroupReward(Player player, int groupId) {
        this.ownerUid = player.getUid();
        this.groupId = groupId;
    }
    
    public boolean hasTakenReward(int starCount) {
        return (takenStars & (1L << starCount)) != 0;
    }

    public void setTakenReward(int starCount) {
        this.takenStars |= 1L << starCount;
        this.save();
    }
    
    public ChallengeReward toProto() {
        var proto = ChallengeReward.newInstance()
                .setGroupId(this.getGroupId())
                .setTakenChallengeReward(this.getTakenStars());
        
        return proto;
    }
    
    public void delete() {
        LunarCore.getGameDatabase().delete(this);
    }
    
    public void save() {
        LunarCore.getGameDatabase().save(this);
    }
}
