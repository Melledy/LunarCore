package emu.lunarcore.game.avatar;

import java.util.Map;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import dev.morphia.annotations.Indexed;

import emu.lunarcore.LunarCore;
import emu.lunarcore.data.excel.AvatarExcel;
import emu.lunarcore.game.player.Player;
import emu.lunarcore.proto.AvatarSkillTreeOuterClass.AvatarSkillTree;
import emu.lunarcore.proto.HeroBasicTypeInfoOuterClass.HeroBasicTypeInfo;

import lombok.Getter;
import lombok.Setter;

@Getter
@Entity(value = "heroPaths", useDiscriminator = false)
public class AvatarHeroPath {
    @Id private int id; // Equivalent to HeroBaseType
    @Indexed private int ownerUid;
    
    private AvatarData data;
    
    @Setter private transient GameAvatar avatar;
    private transient AvatarExcel excel;
    
    @Deprecated // Morphia only!
    public AvatarHeroPath() {
        
    }
    
    public AvatarHeroPath(Player player, AvatarExcel excel) {
        // Set excel avatar id as id
        this.id = excel.getId();
        this.ownerUid = player.getUid();
        this.setExcel(excel);
    }
    
    public void setExcel(AvatarExcel excel) {
        if (this.excel == null) {
            this.excel = excel;
        }
        if (this.data == null) {
            this.data = new AvatarData(excel);
        }
    }
    
    public int getRank() {
        return this.getData().getRank();
    }
    
    public Map<Integer, Integer> getSkills() {
        return this.getData().getSkills();
    }
    
    public HeroBasicTypeInfo toProto() {
        var proto = HeroBasicTypeInfo.newInstance()
                .setBasicTypeValue(this.getId())
                .setRank(this.getRank());
        
        for (var skill : getSkills().entrySet()) {
            proto.addSkillTreeList(AvatarSkillTree.newInstance().setPointId(skill.getKey()).setLevel(skill.getValue()));
        }
        
        return proto;
    }
    
    public void save() {
        LunarCore.getGameDatabase().save(this);
    }
}
