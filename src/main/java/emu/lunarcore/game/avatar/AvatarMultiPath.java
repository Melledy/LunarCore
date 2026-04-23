package emu.lunarcore.game.avatar;

import org.bson.types.ObjectId;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import dev.morphia.annotations.Indexed;

import emu.lunarcore.LunarCore;
import emu.lunarcore.data.excel.AvatarExcel;
import emu.lunarcore.game.enums.ItemMainType;
import emu.lunarcore.game.player.Player;
import emu.lunarcore.proto.AvatarPathInfoOuterClass.AvatarPathInfo;
import emu.lunarcore.proto.AvatarPathSkillTreeOuterClass.AvatarPathSkillTree;
import emu.lunarcore.proto.EquipRelicOuterClass.EquipRelic;
import emu.lunarcore.proto.PlayerSyncScNotifyOuterClass.PlayerSyncScNotify;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import lombok.Getter;
import lombok.Setter;

@Getter
@Entity(value = "multiPaths", useDiscriminator = false)
public class AvatarMultiPath extends BaseAvatar {
    @Id private ObjectId id;
    @Indexed private int ownerUid;
    
    private int excelId;
    private AvatarData data;
    
    @Setter private transient Player owner;
    @Setter private transient GameAvatar avatar;
    private transient AvatarExcel excel;
    
    @Deprecated // Morphia only!
    public AvatarMultiPath() {
        
    }
    
    public AvatarMultiPath(Player player, AvatarExcel excel) {
        // Set excel avatar id as id
        this.excelId = excel.getId();
        this.ownerUid = player.getUid();
        this.owner = player;
        this.setExcel(excel);
    }
    
    public void setExcel(AvatarExcel excel) {
        if (this.excel == null) {
            this.excel = excel;
        }
        if (this.data == null) {
            this.data = new AvatarData(excel);
        }
        this.data.setBaseAvatar(this);
    }
    
    public int getRank() {
        return this.getData().getRank();
    }
    
    public int getEnhanceId() {
        return this.getData().getEnhanceId();
    }
    
    @SuppressWarnings("deprecation")
    public Int2IntMap getSkills() {
        return this.getData().getSkills();
    }
    
    public Int2IntMap getSkillTree() {
        return this.getData().getSkillTree();
    }
    
    // Player sync
    
    public void onSync(PlayerSyncScNotify proto) {
        // TODO
    }
    
    // Proto
    
    public AvatarPathInfo toPathInfoProto() {
        var proto = AvatarPathInfo.newInstance()
                .setAvatarId(this.getExcelId())
                .setRank(this.getRank())
                .setAvatarSkin(this.getData().getSkinId())
                .setEnhanceId(this.getEnhanceId());

        for (var equip : this.getEquips().values()) {
            if (equip.getItemMainType() == ItemMainType.Relic) {
                proto.addEquipRelicList(EquipRelic.newInstance().setSlot(equip.getEquipSlot()).setRelicUniqueId(equip.getInternalUid()));
            } else if (equip.getItemMainType() == ItemMainType.Equipment) {
                proto.setPathEquipmentId(equip.getInternalUid());
            }
        }
        
        for (var skill : this.getSkillTree().int2IntEntrySet()) {
            // Set skill point proto
            var info = AvatarPathSkillTree.newInstance()
                    .setAnchorPointId(skill.getIntKey())
                    .setLevel(skill.getIntValue());
            
            proto.addSkilltreeList(info);
        }
        
        return proto;
    }
    
    // Database
    
    public void save() {
        LunarCore.getGameDatabase().save(this);
    }
}
