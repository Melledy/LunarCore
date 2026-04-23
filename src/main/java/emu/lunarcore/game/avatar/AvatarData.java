package emu.lunarcore.game.avatar;

import dev.morphia.annotations.Entity;
import emu.lunarcore.data.GameData;
import emu.lunarcore.data.excel.AvatarExcel;
import emu.lunarcore.game.inventory.GameItem;
import emu.lunarcore.game.player.Player;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import lombok.Getter;
import lombok.Setter;

/**
 * A helper class that contains information about an avatar's rank.
 */
@Getter
@Entity(useDiscriminator = false)
public class AvatarData {
    @Setter
    private int rank;               // Eidolons
    private Int2IntMap skillTree;   // Skill tree
    private int skinId;             // Avatar skin id
    private int enhanceId;          // Enhance state
    
    @Deprecated
    private Int2IntMap skills;      // Skill tree (old)
    
    @Setter
    private transient BaseAvatar baseAvatar;
    private transient Int2ObjectMap<GameItem> equips;
        
    @Deprecated // Morphia only
    public AvatarData() {
        this.equips = new Int2ObjectOpenHashMap<>();
    }
    
    public AvatarData(AvatarExcel excel) {
        this();
        this.skillTree = new Int2IntOpenHashMap();
        this.addDefaultSkills(excel);
    }
    
    public Player getOwner() {
        return this.getBaseAvatar().getOwner();
    }
    
    private void addDefaultSkills(AvatarExcel excel) {
        for (var skillExcel : excel.getDefaultSkillTrees()) {
            // Check enhance id
            if (skillExcel.getEnhancedID() != this.getEnhanceId()) {
                continue;
            }
            
            // Add skill tree
            this.skillTree.put(skillExcel.getAnchorId(), skillExcel.getLevel());
        }
    }
    
    public boolean setEnhanceId(int enhanceId) {
        // Sanity check
        if (this.enhanceId == enhanceId) {
            return false;
        }
        
        // Update enhance state
        this.enhanceId = enhanceId;
        
        // Success
        return true;
    }
    
    public void setSkinId(int skinId) {
        this.skinId = skinId;
    }
    
    /**
     * Converts skilltrees to skill anchor types
     */
    public void fixSkills() {
        // Check skills
        if (this.skills == null || this.skills.isEmpty()) {
            return;
        }
        
        // Create skill tree
        this.skillTree = new Int2IntOpenHashMap();
        
        // Convert from point id to anchor id
        for (var entry : this.skills.int2IntEntrySet()) {
            int pointId = entry.getIntKey();
            int level = entry.getIntValue();
            
            var excel = GameData.getAvatarSkillTreeExcelMap().get(pointId, level);
            if (excel == null) continue;
            
            // Add skill tree
            this.skillTree.put(excel.getAnchorId(), excel.getLevel());
        }
        
        // Clear old skills
        this.skills = null;
    }
}
