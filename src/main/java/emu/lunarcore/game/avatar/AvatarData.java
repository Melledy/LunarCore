package emu.lunarcore.game.avatar;

import dev.morphia.annotations.Entity;
import emu.lunarcore.data.excel.AvatarExcel;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import lombok.Getter;
import lombok.Setter;

/**
 * A helper class that contains information about an avatar's rank.
 */
@Entity(useDiscriminator = false)
public class AvatarData {
    @Getter @Setter
    private int rank; // Eidolons
    @Getter
    private Int2IntMap skills; // Skill tree
        
    @Deprecated
    public AvatarData() {
        
    }
    
    public AvatarData(AvatarExcel excel) {
        this.skills = new Int2IntOpenHashMap();
        for (var skillTree : excel.getDefaultSkillTrees()) {
            this.skills.put(skillTree.getPointID(), skillTree.getLevel());
        }
    }
}
