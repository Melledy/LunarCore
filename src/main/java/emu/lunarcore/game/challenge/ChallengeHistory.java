package emu.lunarcore.game.challenge;

import org.bson.types.ObjectId;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import dev.morphia.annotations.Indexed;
import emu.lunarcore.LunarRail;
import emu.lunarcore.game.player.Player;
import emu.lunarcore.proto.ChallengeOuterClass.Challenge;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@Entity(value = "challenge", useDiscriminator = false)
public class ChallengeHistory {
    @Id
    private ObjectId id;
    
    @Indexed
    private int ownerUid;
    
    private int challengeId;
    private int takenReward;
    private int stars;
    
    @Deprecated // Morphia
    public ChallengeHistory() {}
    
    public ChallengeHistory(Player player, int challengeId) {
        this.ownerUid = player.getUid();
        this.challengeId = challengeId;
    }
    
    public Challenge toProto() {
        var proto = Challenge.newInstance()
                .setChallengeId(this.getChallengeId())
                .setTakenReward(this.getTakenReward())
                .setStars(this.getStars());
        
        return proto;
    }
    
    public void delete() {
        LunarRail.getGameDatabase().delete(this);
    }
    
    public void save() {
        LunarRail.getGameDatabase().save(this);
    }
}
