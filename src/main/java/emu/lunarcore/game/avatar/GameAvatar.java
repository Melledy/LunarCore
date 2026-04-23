package emu.lunarcore.game.avatar;

import org.bson.types.ObjectId;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import dev.morphia.annotations.Indexed;

import emu.lunarcore.GameConstants;
import emu.lunarcore.LunarCore;
import emu.lunarcore.data.GameData;
import emu.lunarcore.data.excel.AvatarExcel;
import emu.lunarcore.game.enums.ItemMainType;
import emu.lunarcore.game.player.Player;
import emu.lunarcore.game.player.lineup.PlayerLineup;
import emu.lunarcore.game.scene.Scene;
import emu.lunarcore.game.scene.entity.GameEntity;
import emu.lunarcore.proto.AssistSimpleInfoOuterClass.AssistSimpleInfo;
import emu.lunarcore.proto.AvatarOuterClass.Avatar;
import emu.lunarcore.proto.AvatarPathInfoOuterClass.AvatarPathInfo;
import emu.lunarcore.proto.AvatarPathSkillTreeOuterClass.AvatarPathSkillTree;
import emu.lunarcore.proto.AvatarSkillTreeOuterClass.AvatarSkillTree;
import emu.lunarcore.proto.AvatarTypeOuterClass.AvatarType;
import emu.lunarcore.proto.BattleAvatarOuterClass.BattleAvatar;
import emu.lunarcore.proto.BattleEquipmentOuterClass.BattleEquipment;
import emu.lunarcore.proto.BattleRelicOuterClass.BattleRelic;
import emu.lunarcore.proto.DisplayAvatarDetailInfoOuterClass.DisplayAvatarDetailInfo;
import emu.lunarcore.proto.DisplayEquipmentInfoOuterClass.DisplayEquipmentInfo;
import emu.lunarcore.proto.DisplayRelicInfoOuterClass.DisplayRelicInfo;
import emu.lunarcore.proto.EquipRelicOuterClass.EquipRelic;
import emu.lunarcore.proto.LineupAvatarOuterClass.LineupAvatar;
import emu.lunarcore.proto.MotionInfoOuterClass.MotionInfo;
import emu.lunarcore.proto.PlayerSyncScNotifyOuterClass.PlayerSyncScNotify;
import emu.lunarcore.proto.SceneActorInfoOuterClass.SceneActorInfo;
import emu.lunarcore.proto.SceneEntityInfoOuterClass.SceneEntityInfo;
import emu.lunarcore.proto.SpBarInfoOuterClass.SpBarInfo;
import emu.lunarcore.server.packet.send.PacketPlayerSyncScNotify;
import emu.lunarcore.util.Position;

import it.unimi.dsi.fastutil.ints.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Entity(value = "avatars", useDiscriminator = false)
public class GameAvatar extends BaseAvatar implements GameEntity {
    @Id private ObjectId id;
    @Indexed @Getter private int ownerUid; // Uid of player that this avatar belongs to

    private transient Player owner;
    private transient AvatarExcel excel;
    
    private AvatarData data;
    protected int avatarId; // Id of avatar in the excels
    @Setter private int level;
    @Setter private int exp;
    @Setter private int promotion;
    @Setter private boolean marked;
    
    private int rewards; // Previously known as "taken rewards"
    private long timestamp;
    
    @Getter(AccessLevel.NONE) protected int currentHp;
    @Getter(AccessLevel.NONE) protected int currentSp;
    @Getter(AccessLevel.NONE) protected int extraLineupHp;
    @Getter(AccessLevel.NONE) protected int extraLineupSp;

    private transient int entityId;
    private transient Int2LongMap buffs;
    private transient AvatarMultiPath multiPath;

    @Deprecated // Morphia only
    public GameAvatar() {
        this.buffs = Int2LongMaps.synchronize(new Int2LongOpenHashMap());
        this.level = 1;
        this.currentHp = 10000;
        this.currentSp = 0;
    }
    
    public GameAvatar(int avatarId) {
        this(GameData.getAvatarExcelMap().get(avatarId));
    }

    public GameAvatar(AvatarExcel excel) {
        this();
        this.avatarId = excel.getId();
        this.timestamp = System.currentTimeMillis() / 1000;
        this.setExcel(excel);
    }
    
    @Override
    public Scene getScene() {
        return this.getOwner().getScene();
    }
    
    @Override
    public int getExcelId() {
        return this.avatarId;
    }
    
    // Avatar id that is used in scenes
    public int getActorId() {
        return this.avatarId;
    }
    
    // Avatar id that is used in battles
    public int getBattleAvatarId() {
        return this.getExcel().getAvatarID();
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

    public void setOwner(Player player) {
        this.owner = player;
        this.ownerUid = player.getUid();
    }
    
    @Override
    public void setEntityId(int entityId) {
        this.entityId = entityId;
    }
    
    @Override
    public Position getPos() {
        return this.getOwner().getPos();
    }
    
    @Override
    public Position getRot() {
        return this.getOwner().getRot();
    }
    
    public int getHeadIconId() {
        return 200000 + this.getAvatarId();
    }
    
    public AvatarType getAvatarType() {
        return AvatarType.AVATAR_FORMAL_TYPE;
    }
    
    public boolean isHero() {
        return this.getAvatarId() == GameConstants.TRAILBLAZER_AVATAR_ID;
    }
    
    public boolean hasMultiPath() {
        return GameData.getMultiplePathAvatarExcelMap().containsKey(this.getAvatarId());
    }
    
    public int getEnhanceId() {
        return this.getData().getEnhanceId();
    }
    
    public int getMaxHp() {
        return 10000;
    }

    public int getMaxSp() {
        return 10000;
    }
    
    public int getCurrentHp(PlayerLineup lineup) {
        return !lineup.isExtraLineup() ? this.currentHp : this.extraLineupHp;
    }
    
    public int getCurrentSp(PlayerLineup lineup) {
        return !lineup.isExtraLineup() ? this.currentSp : this.extraLineupSp;
    }
    
    public void setCurrentHp(PlayerLineup lineup, int amount) {
        amount = Math.max(Math.min(amount, this.getMaxHp()), 0);
        if (!lineup.isExtraLineup()) {
            this.currentHp = amount; 
        } else {
            this.extraLineupHp = amount; 
        }
    }

    public void setCurrentSp(PlayerLineup lineup, int amount) {
        amount = Math.max(Math.min(amount, getMaxSp()), 0);
        if (!lineup.isExtraLineup()) {
            this.currentSp = amount; 
        } else {
            this.extraLineupSp = amount; 
        }
    }
    
    public boolean isAlive() {
        return this.isAlive(this.getOwner().getCurrentLineup());
    }

    public boolean isAlive(PlayerLineup lineup) {
        return this.getCurrentHp(lineup) > 0;
    }
    
    public int getRank() {
        return this.getData().getRank();
    }
    
    public void setRank(int rank) {
        this.getData().setRank(rank);
    }
    
    @SuppressWarnings("deprecation")
    public Int2IntMap getSkills() {
        return this.getData().getSkills();
    }
    
    public Int2IntMap getSkillTree() {
        return this.getData().getSkillTree();
    }
    
    public void setMultiPath(AvatarMultiPath multiPath) {
        // Clear prev set hero path from avatar
        if (this.multiPath != null) {
            this.multiPath.setAvatar(null);
        }
        
        this.data = multiPath.getData();
        this.excel = multiPath.getExcel(); // DO NOT USE GameAvatar::setExcel for this
        this.multiPath = multiPath;
        this.multiPath.setAvatar(this);
    }
    
    // Rewards
    
    public boolean setRewards(int flag) {
        if (this.rewards != flag) {
            this.rewards = flag;
            return true;
        }
        
        return false;
    }
    
    public boolean hasTakenReward(int promotion) {
        return (this.rewards & (1 << promotion)) != 0;
    }
    
    public void takeReward(int promotion) {
        this.rewards |= 1 << promotion;
    }
    
    // Skin
    
    public void setSkin(int skinId) {
        // Sanity
        if (skinId == this.getData().getSkinId()) {
            return;
        }
        
        // Verify skin excel
        if (skinId != 0) {
            var excel = GameData.getAvatarSkinExcelMap().get(skinId);
            if (excel == null || excel.getAvatarID() != this.getExcelId()) {
                return;
            }
        }
        
        // Set skin
        this.getData().setSkinId(skinId);
        
        if (this.getMultiPath() != null) {
            this.getMultiPath().save();
        } else {
            this.save(); 
        }
        
        // Update avatar to client
        this.getOwner().sendPacket(new PacketPlayerSyncScNotify(this));
    }
    
    // Buffs
    
    public void addBuff(int buffId, int duration) {
        this.buffs.put(buffId, System.currentTimeMillis() + (duration * 1000));
    }
    
    // Player sync
    
    public void onSync(PlayerSyncScNotify proto) {
        // Add to avatar sync
        proto.getMutableAvatarSync().addAvatarList(this.toProto());
        
        // Add multi path
        if (this.hasMultiPath()) {
            proto.getMutableAvatarSync().addAvatarPathInfoList(this.getMultiPath().toPathInfoProto());
        } else {
            proto.getMutableAvatarSync().addAvatarPathInfoList(this.toPathInfoProto());
        }
    }
    
    // Proto

    public Avatar toProto() {
        var proto = Avatar.newInstance()
                .setIsMarked(this.isMarked())
                .setBaseAvatarId(this.getAvatarId())
                .setLevel(this.getLevel())
                .setExp(this.getExp())
                .setPromotion(this.getPromotion())
                .setIsMarked(this.isMarked()) 
                .setFirstMetTimestamp(this.getTimestamp());
        
        for (int i = 0; i < this.getPromotion(); i++) {
            if (this.hasTakenReward(i)) {
                proto.addTakenRewards(i);
            }
        }
        
        if (this.isHero()) {
            proto.setChangedAvatarType(this.getMultiPath().getExcelId());
        } else {
            proto.setChangedAvatarType(this.getAvatarId());
        }
        
        return proto;
    }
    
    public AvatarPathInfo toPathInfoProto() {
        var proto = AvatarPathInfo.newInstance()
                .setAvatarId(this.getAvatarId())
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

    public LineupAvatar toLineupAvatarProto(PlayerLineup lineup, int slot) {
        var proto = LineupAvatar.newInstance()
                .setAvatarType(this.getAvatarType())
                .setId(this.getAvatarId())
                .setSpBar(SpBarInfo.newInstance().setCurSp(this.getCurrentSp(lineup)).setMaxSp(this.getMaxSp()))
                .setHp(this.getCurrentHp(lineup))
                .setSlot(slot);
        
        return proto;
    }

    @Override
    public SceneEntityInfo toSceneEntityProto() {
        var proto = SceneEntityInfo.newInstance()
                .setEntityId(this.getEntityId())
                .setMotion(MotionInfo.newInstance().setPos(this.getPos().toProto()).setRot(this.getRot().toProto()))
                .setActor(SceneActorInfo.newInstance().setBaseAvatarId(this.getActorId()).setAvatarType(this.getAvatarType()));

        return proto;
    }

    public BattleAvatar toBattleProto(PlayerLineup lineup, int index) {
        var proto = BattleAvatar.newInstance()
                .setAvatarType(this.getAvatarType())
                .setId(this.getBattleAvatarId())
                .setLevel(this.getLevel())
                .setPromotion(this.getPromotion())
                .setRank(this.getRank())
                .setIndex(index)
                .setAvatarEnhanceId(this.getEnhanceId())
                .setWorldLevel(this.getOwner().getWorldLevel());
        
        // Set Hp/Sp
        if (lineup != null) {
            proto.setHp(this.getCurrentHp(lineup))
                .setSpBar(SpBarInfo.newInstance().setCurSp(this.getCurrentSp(lineup)).setMaxSp(this.getMaxSp()));
        } else {
            proto.setHp(this.getMaxHp())
                .setSpBar(SpBarInfo.newInstance().setCurSp(this.getMaxSp()).setMaxSp(this.getMaxSp()));
        }

        // Skills
        for (var skill : this.getSkillTree().int2IntEntrySet()) {
            // Get current skill excel with enhance id
            var skillExcel = GameData.getAvatarSkilltree(this.getExcelId(), this.getEnhanceId(), skill.getIntKey(), skill.getIntValue());
            if (skillExcel == null) continue;
            
            // Encode skilltree info
            var info = AvatarSkillTree.newInstance()
                    .setPointId(skillExcel.getPointID())
                    .setLevel(skill.getIntValue());
            
            proto.addSkilltreeList(info);
        }

        // Build equips
        for (var equip : this.getEquips().values()) {
            if (equip.getItemMainType() == ItemMainType.Relic) {
                // Build battle relic proto
                var relic = BattleRelic.newInstance()
                        .setId(equip.getItemId())
                        .setLevel(equip.getLevel())
                        .setUniqueId(equip.getInternalUid())
                        .setMainAffixId(equip.getMainAffix());

                if (equip.getSubAffixes() != null) {
                    for (var subAffix : equip.getSubAffixes()) {
                        relic.addSubAffixList(subAffix.toProto());
                    }
                }

                proto.addRelicList(relic);
            } else if (equip.getItemMainType() == ItemMainType.Equipment) {
                // Build battle equipment proto
                var equipment = BattleEquipment.newInstance()
                        .setId(equip.getItemId())
                        .setLevel(equip.getLevel())
                        .setPromotion(equip.getPromotion())
                        .setRank(equip.getRank());

                proto.addEquipmentList(equipment);
            }
        }

        return proto;
    }

    public DisplayAvatarDetailInfo toDisplayAvatarProto() {
        var proto = DisplayAvatarDetailInfo.newInstance()
                .setAvatarId(this.getExcel().getAvatarID())
                .setLevel(this.getLevel())
                .setExp(this.getExp())
                .setPromotion(this.getPromotion())
                .setRank(this.getRank());
        
        // Skills
        for (var skill : this.getSkillTree().int2IntEntrySet()) {
            // Get current skill excel with enhance id
            var skillExcel = GameData.getAvatarSkilltree(this.getExcelId(), this.getEnhanceId(), skill.getIntKey(), skill.getIntValue());
            if (skillExcel == null) continue;
            
            // Encode skilltree info
            var info = AvatarSkillTree.newInstance()
                    .setPointId(skillExcel.getPointID())
                    .setLevel(skill.getIntValue());
            
            proto.addSkilltreeList(info);
        }
        
        // Build equips
        for (var equip : this.getEquips().values()) {
            if (equip.getItemMainType() == ItemMainType.Relic) {
                // Build display relic proto
                var relic = DisplayRelicInfo.newInstance()
                        .setTid(equip.getItemId())
                        .setLevel(equip.getLevel())
                        .setExp(equip.getExp())
                        .setSlot(equip.getEquipSlot())
                        .setMainAffixId(equip.getMainAffix());

                if (equip.getSubAffixes() != null) {
                    for (var subAffix : equip.getSubAffixes()) {
                        relic.addSubAffixList(subAffix.toProto());
                    }
                }

                proto.addRelicList(relic);
            } else if (equip.getItemMainType() == ItemMainType.Equipment) {
                // Build display equipment proto
                var equipment = DisplayEquipmentInfo.newInstance()
                        .setTid(equip.getItemId())
                        .setLevel(equip.getLevel())
                        .setExp(equip.getExp())
                        .setPromotion(equip.getPromotion())
                        .setRank(equip.getRank());

                proto.setEquipment(equipment);
            }
        }
        
        return proto;
    }

    public AssistSimpleInfo toAssistSimpleProto() {
        var proto = AssistSimpleInfo.newInstance()
                .setAvatarId(this.getAvatarId())
                .setLevel(this.getLevel());
        
        return proto;
    }

    // Database

    public void save() {
        // Save avatar
        LunarCore.getGameDatabase().save(this);
        // Save hero path
        if (this.getMultiPath() != null) {
            this.getMultiPath().save();
        }
    }
}
