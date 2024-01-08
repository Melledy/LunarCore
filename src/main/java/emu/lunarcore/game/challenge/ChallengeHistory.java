package emu.lunarcore.game.challenge;

import org.bson.types.ObjectId;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import dev.morphia.annotations.Indexed;
import emu.lunarcore.LunarCore;
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
    private int groupId;
    private int takenReward;
    private int stars;
    private int score;
    
    @Deprecated // Morphia
    public ChallengeHistory() {}
    
    public ChallengeHistory(Player player, int challengeId) {
        this.ownerUid = player.getUid();
        this.challengeId = challengeId;
    }
    
    public void setStars(int stars) {
        this.stars = Math.max(this.stars, stars);
    }
    
    public int getTotalStars() {
        int total = 0;
        for (int i = 0; i < 3; i++) {
            total += (this.stars & (1 << i)) != 0 ? 1 : 0;
        }
        return total;
    }
    
    public void setScore(int score) {
        this.score = score;
    }
    
    public Challenge toProto() {
        var proto = Challenge.newInstance()
                .setChallengeId(this.getChallengeId())
                .setTakenReward(this.getTakenReward())
                .setScore(this.getScore())
                .setStars(this.getStars());
        
        return proto;
    }
    
    public void delete() {
        LunarCore.getGameDatabase().delete(this);
    }
    
    public void save() {
        LunarCore.getGameDatabase().save(this);
    }
}
