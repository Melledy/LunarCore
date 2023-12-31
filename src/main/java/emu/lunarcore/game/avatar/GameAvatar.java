package emu.lunarcore.game.avatar;

import java.util.Map;

import org.bson.types.ObjectId;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import dev.morphia.annotations.Indexed;
import emu.lunarcore.GameConstants;
import emu.lunarcore.LunarCore;
import emu.lunarcore.data.GameData;
import emu.lunarcore.data.excel.AvatarExcel;
import emu.lunarcore.game.enums.ItemMainType;
import emu.lunarcore.game.inventory.GameItem;
import emu.lunarcore.game.player.Player;
import emu.lunarcore.game.player.lineup.PlayerLineup;
import emu.lunarcore.game.scene.Scene;
import emu.lunarcore.game.scene.entity.GameEntity;
import emu.lunarcore.proto.AvatarOuterClass.Avatar;
import emu.lunarcore.proto.AvatarSkillTreeOuterClass.AvatarSkillTree;
import emu.lunarcore.proto.AvatarTypeOuterClass.AvatarType;
import emu.lunarcore.proto.BattleAvatarOuterClass.BattleAvatar;
import emu.lunarcore.proto.BattleEquipmentOuterClass.BattleEquipment;
import emu.lunarcore.proto.BattleRelicOuterClass.BattleRelic;
import emu.lunarcore.proto.EquipRelicOuterClass.EquipRelic;
import emu.lunarcore.proto.LineupAvatarOuterClass.LineupAvatar;
import emu.lunarcore.proto.MotionInfoOuterClass.MotionInfo;
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
public class GameAvatar implements GameEntity {
    @Id private ObjectId id;
    @Indexed @Getter private int ownerUid; // Uid of player that this avatar belongs to

    private transient Player owner;
    private transient AvatarExcel excel;
    
    private int avatarId; // Id of avatar in the excels
    private AvatarData data;
    @Setter private int level;
    @Setter private int exp;
    @Setter private int promotion;
    
    private int rewards; // Previously known as "taken rewards"
    private long timestamp;
    
    @Getter(AccessLevel.NONE) private int currentHp;
    @Getter(AccessLevel.NONE) private int currentSp;
    @Getter(AccessLevel.NONE) private int extraLineupHp;
    @Getter(AccessLevel.NONE) private int extraLineupSp;

    private transient int entityId;
    private transient Int2ObjectMap<GameItem> equips;
    private transient Int2LongMap buffs;
    private transient AvatarHeroPath heroPath;

    @Deprecated // Morphia only
    public GameAvatar() {
        this.equips = new Int2ObjectOpenHashMap<>();
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
    
    public GameAvatar(AvatarHeroPath path) {
        this();
        this.avatarId = GameConstants.TRAILBLAZER_AVATAR_ID;
        this.timestamp = System.currentTimeMillis() / 1000;
        this.setHeroPath(path);
    }
    
    @Override
    public Scene getScene() {
        return this.getOwner().getScene();
    }
    
    public void setExcel(AvatarExcel excel) {
        if (this.excel == null) {
            this.excel = excel;
        }
        if (this.data == null) {
            this.data = new AvatarData(excel);
        }
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
    
    public boolean isHero() {
        return GameData.getHeroExcelMap().containsKey(this.getAvatarId());
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
        amount = Math.max(Math.min(amount, 10000), 0);
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
    
    public Map<Integer, Integer> getSkills() {
        return this.getData().getSkills();
    }
    
    public void setHeroPath(AvatarHeroPath heroPath) {
        // Clear prev set hero path from avatar
        if (this.getHeroPath() != null) {
            this.getHeroPath().setAvatar(null);
        }
        
        this.data = heroPath.getData();
        this.excel = heroPath.getExcel(); // DO NOT USE GameAvatar::setExcel for this
        this.heroPath = heroPath;
        this.heroPath.setAvatar(this);
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
    
    // Buffs
    
    public void addBuff(int buffId, int duration) {
        this.buffs.put(buffId, System.currentTimeMillis() + (duration * 1000));
    }

    // Equips

    public GameItem getEquipBySlot(int slot) {
        return this.getEquips().get(slot);
    }

    public GameItem getEquipment() {
        return this.getEquips().get(GameConstants.EQUIPMENT_SLOT_ID);
    }

    public boolean equipItem(GameItem item) {
        // Sanity check
        int slot = item.getEquipSlot();
        if (slot == 0) return false;

        // Check if other avatars have this item equipped
        GameAvatar otherAvatar = getOwner().getAvatarById(item.getEquipAvatar());
        if (otherAvatar != null) {
            // Unequip this item from the other avatar
            if (otherAvatar.unequipItem(slot) != null) {
                getOwner().sendPacket(new PacketPlayerSyncScNotify(otherAvatar));
            }
            // Swap with other avatar
            if (getEquips().containsKey(slot)) {
                GameItem toSwap = this.getEquipBySlot(slot);
                otherAvatar.equipItem(toSwap);
            }
        } else if (getEquips().containsKey(slot)) {
            // Unequip item in current slot if it exists
            GameItem unequipped = unequipItem(slot);
            if (unequipped != null) {
                getOwner().sendPacket(new PacketPlayerSyncScNotify(unequipped));
            }
        }

        // Set equip
        getEquips().put(slot, item);

        // Save equip if equipped avatar was changed
        if (item.setEquipAvatar(this.getAvatarId())) {
            item.save();
        }

        // Send packet
        getOwner().sendPacket(new PacketPlayerSyncScNotify(this, item));

        return true;
    }

    public GameItem unequipItem(int slot) {
        GameItem item = getEquips().remove(slot);

        if (item != null) {
            item.setEquipAvatar(0);
            item.save();
            return item;
        }

        return null;
    }

    // Proto

    public Avatar toProto() {
        var proto = Avatar.newInstance()
                .setBaseAvatarId(this.getAvatarId())
                .setLevel(this.getLevel())
                .setExp(this.getExp())
                .setPromotion(this.getPromotion())
                .setRank(this.getRank())
                .setFirstMetTimestamp(this.getTimestamp());

        for (var equip : this.getEquips().values()) {
            if (equip.getItemMainType() == ItemMainType.Relic) {
                proto.addEquipRelicList(EquipRelic.newInstance().setSlot(equip.getEquipSlot()).setRelicUniqueId(equip.getInternalUid()));
            } else if (equip.getItemMainType() == ItemMainType.Equipment) {
                proto.setEquipmentUniqueId(equip.getInternalUid());
            }
        }

        for (var skill : getSkills().entrySet()) {
            proto.addSkilltreeList(AvatarSkillTree.newInstance().setPointId(skill.getKey()).setLevel(skill.getValue()));
        }
        
        for (int i = 0; i < this.getPromotion(); i++) {
            if (this.hasTakenReward(i)) {
                proto.addTakenRewards(i);
            }
        }
        
        return proto;
    }

    public LineupAvatar toLineupAvatarProto(PlayerLineup lineup, int slot) {
        var proto = LineupAvatar.newInstance()
                .setAvatarType(AvatarType.AVATAR_FORMAL_TYPE)
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
                .setActor(SceneActorInfo.newInstance().setBaseAvatarId(this.getAvatarId()).setAvatarType(AvatarType.AVATAR_FORMAL_TYPE));

        return proto;
    }

    public BattleAvatar toBattleProto(PlayerLineup lineup, int index) {
        var proto = BattleAvatar.newInstance()
                .setAvatarType(AvatarType.AVATAR_FORMAL_TYPE)
                .setId(this.getExcel().getAvatarID())
                .setLevel(this.getLevel())
                .setPromotion(this.getPromotion())
                .setRank(this.getRank())
                .setIndex(index)
                .setHp(this.getCurrentHp(lineup))
                .setSpBar(SpBarInfo.newInstance().setCurSp(this.getCurrentSp(lineup)).setMaxSp(this.getMaxSp()))
                .setWorldLevel(this.getOwner().getWorldLevel());

        // Skill tree
        for (var skill : getSkills().entrySet()) {
            proto.addSkilltreeList(AvatarSkillTree.newInstance().setPointId(skill.getKey()).setLevel(skill.getValue()));
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

    // Database

    public void save() {
        // Save avatar
        LunarCore.getGameDatabase().save(this);
        // Save hero path
        if (this.getHeroPath() != null) {
            this.getHeroPath().save();
        }
    }
}
