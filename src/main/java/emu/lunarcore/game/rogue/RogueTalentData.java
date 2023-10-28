package emu.lunarcore.game.rogue;

import org.bson.types.ObjectId;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import dev.morphia.annotations.Indexed;
import emu.lunarcore.LunarCore;
import emu.lunarcore.game.player.Player;
import lombok.Getter;

@Getter
@Entity(value = "rogueTalents", useDiscriminator = false)
public class RogueTalentData {
    @Id
    private ObjectId id;
    
    @Indexed
    private int ownerUid;
    private int talentId;
    
    @Deprecated // Morphia only
    public RogueTalentData() {}
    
    public RogueTalentData(Player player, int talentId) {
        this.ownerUid = player.getUid();
        this.talentId = talentId;
    }
    
    public void save() {
        LunarCore.getGameDatabase().save(this);
    }
}
