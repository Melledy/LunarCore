package emu.lunarcore.game.avatar;

import java.util.HashMap;
import java.util.Map;

import dev.morphia.annotations.Entity;
import emu.lunarcore.data.excel.AvatarExcel;
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
    private Map<Integer, Integer> skills; // Skill tree
        
    @Deprecated
    public AvatarData() {
        
    }
    
    public AvatarData(AvatarExcel excel) {
        this.skills = new HashMap<>();
        for (var skillTree : excel.getDefaultSkillTrees()) {
            this.skills.put(skillTree.getPointID(), skillTree.getLevel());
        }
    }
}
