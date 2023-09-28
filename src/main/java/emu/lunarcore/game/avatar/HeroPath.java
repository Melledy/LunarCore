package emu.lunarcore.game.avatar;

import java.util.HashMap;
import java.util.Map;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import dev.morphia.annotations.Indexed;
import emu.lunarcore.LunarRail;
import emu.lunarcore.data.excel.AvatarExcel;
import emu.lunarcore.game.player.Player;
import emu.lunarcore.proto.AvatarSkillTreeOuterClass.AvatarSkillTree;
import emu.lunarcore.proto.HeroBasicTypeInfoOuterClass.HeroBasicTypeInfo;
import lombok.Getter;
import lombok.Setter;

@Getter
@Entity(value = "heroPaths", useDiscriminator = false)
public class HeroPath {
    @Id 
    private int id; // Equivalent to HeroBaseType
    @Indexed 
    private int ownerUid;
    
    private AvatarRank rank;
    private Map<Integer, Integer> skills;
    
    @Setter private transient GameAvatar avatar;
    @Setter private transient AvatarExcel excel;
    
    @Deprecated // Morphia only!
    public HeroPath() {
        
    }
    
    public HeroPath(Player player, AvatarExcel excel) {
        // Set excel avatar id as id
        this.id = excel.getId();
        this.ownerUid = player.getUid();
        this.excel = excel;

        // Set defaults
        this.rank = new AvatarRank();
        this.skills = new HashMap<>();
        
        // Add skills
        for (var skillTree : excel.getDefaultSkillTrees()) {
            this.skills.put(skillTree.getPointID(), skillTree.getLevel());
        }
    }
    
    public HeroBasicTypeInfo toProto() {
        var proto = HeroBasicTypeInfo.newInstance()
                .setBasicTypeValue(this.getId())
                .setRank(this.getRank().getValue());
        
        for (var skill : getSkills().entrySet()) {
            proto.addSkillTreeList(AvatarSkillTree.newInstance().setPointId(skill.getKey()).setLevel(skill.getValue()));
        }
        
        return proto;
    }
    
    public void save() {
        LunarRail.getGameDatabase().save(this);
    }
}
