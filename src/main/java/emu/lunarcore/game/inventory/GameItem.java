package emu.lunarcore.game.inventory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bson.types.ObjectId;

import dev.morphia.annotations.*;
import emu.lunarcore.LunarCore;
import emu.lunarcore.data.GameData;
import emu.lunarcore.data.GameDepot;
import emu.lunarcore.data.excel.ItemExcel;
import emu.lunarcore.data.excel.RelicMainAffixExcel;
import emu.lunarcore.data.excel.RelicSubAffixExcel;
import emu.lunarcore.game.avatar.BaseAvatar;
import emu.lunarcore.game.enums.AvatarPropertyType;
import emu.lunarcore.game.enums.ItemMainType;
import emu.lunarcore.game.player.Player;
import emu.lunarcore.proto.EquipmentOuterClass.Equipment;
import emu.lunarcore.proto.ItemOuterClass.Item;
import emu.lunarcore.proto.MaterialOuterClass.Material;
import emu.lunarcore.proto.PileItemOuterClass.PileItem;
import emu.lunarcore.proto.PlayerSyncScNotifyOuterClass.PlayerSyncScNotify;
import emu.lunarcore.proto.RelicOuterClass.Relic;
import emu.lunarcore.server.game.Syncable;
import emu.lunarcore.util.Utils;
import emu.lunarcore.util.WeightedList;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import lombok.Getter;
import lombok.Setter;

@Getter
@Entity(value = "items", useDiscriminator = false)
public class GameItem implements Syncable {
    @Id private ObjectId id;
    @Indexed private int ownerUid; // Uid of player that this avatar belongs to

    private transient int internalUid; // Internal unique id of item
    private transient ItemExcel excel;

    private int itemId;
    private int count;

    @Setter private int level;
    @Setter private int exp;
    @Setter private int totalExp;
    @Setter private int promotion;
    @Setter private int rank; // Superimpose
    @Setter private boolean locked;
    @Setter private boolean discarded;
    
    @Setter private int mainAffix;
    private List<GameItemSubAffix> subAffixes;
    private transient List<GameItemSubAffix> reforgedSubAffixes;
    
    @Indexed private ObjectId equipAvatarId; // Object id of the avatar this item is equipped to
    private transient BaseAvatar equipAvatar;
    
    @LoadOnly @AlsoLoad("equipAvatar")
    private int equipAvatarExcelId; // Deprecated legacy field

    @Deprecated
    public GameItem() {
        // Morphia only
    }

    public GameItem(int itemId) {
        this(GameData.getItemExcelMap().get(itemId));
    }

    public GameItem(int itemId, int count) {
        this(GameData.getItemExcelMap().get(itemId), count);
    }

    public GameItem(ItemExcel data) {
        this(data, 1);
    }
    
    public GameItem(ItemExcel excel, int count) {
        this(excel, count, 0, null);
    }
    
    public GameItem(ItemExcel excel, int count, int mainAffix) {
        this(excel, count, mainAffix, null);
    }

    public GameItem(ItemExcel excel, int count, int mainAffix, IntSet subAffixes) {
        this.itemId = excel.getId();
        this.excel = excel;

        switch (excel.getItemMainType()) {
            case Virtual:
                this.count = count;
                break;
            case Equipment:
                this.count = 1;
                this.level = 1;
                this.rank = 1;
                break;
            case Relic:
                this.count = 1;
                // Init affixes
                if (getExcel().getRelicExcel() != null) {
                    // Main affix
                    if (mainAffix > 0) {
                        // Set custom main affix
                        this.mainAffix = mainAffix;
                    } else {
                        // Randomly generate main affix
                        var affix = GameDepot.getRandomRelicMainAffix(getExcel().getRelicExcel().getMainAffixGroup());
                        if (affix != null) {
                            this.mainAffix = affix.getAffixID();
                        }
                    } 
                    // Sub affixes
                    if (subAffixes != null) {
                        // Set custom sub affixes
                        for (int subAffixId : subAffixes) {
                            // Get sub affix excel
                            var subAffix = GameData.getRelicSubAffixExcelMap().get(excel.getRelicExcel().getSubAffixGroup(), subAffixId);
                            if (subAffix == null) continue;
                            
                            // Set count
                            this.addSubAffix(new GameItemSubAffix(subAffix, 1));
                        }
                    }
                    // Get base sub affixes
                    int baseSubAffixes = Math.min(Math.max(getExcel().getRarity().getVal() - 2, 0), 3);
                    int subAffixSize = Utils.randomRange(baseSubAffixes, baseSubAffixes + 1) - this.getSubAffixListSize();
                    this.addRandomSubAffixes(subAffixSize);
                    // Sort sub affixes
                    this.sortSubAffixes();
                }
                break;
            default:
                this.count = Math.min(count, excel.getPileLimit());
        }
    }

    public void setOwner(Player player) {
        this.ownerUid = player.getUid();
        this.internalUid = player.getInventory().getNextItemInternalUid();
    }

    public void setExcel(ItemExcel excel) {
        this.excel = excel;
    }

    public ItemMainType getItemMainType() {
        return excel.getItemMainType();
    }

    public int getEquipSlot() {
        return excel.getEquipSlot();
    }

    public boolean isEquipped() {
        return this.getEquipAvatarId() != null;
    }

    public boolean isDestroyable() {
        return !this.isLocked() && !this.isEquipped();
    }

    public boolean setCount(int count) {
        if (this.count != count) {
            this.count = count;
            return true;
        }
        
        return false;
    }

    public boolean setEquipAvatar(BaseAvatar baseAvatar) {
        if (baseAvatar == null && this.isEquipped()) {
            this.equipAvatarId = null;
            this.equipAvatar = null;
            this.equipAvatarExcelId = 0; // Legacy field
            return true;
        } else if (this.equipAvatarId != baseAvatar.getId()) {
            this.equipAvatarId = baseAvatar.getId();
            this.equipAvatar = baseAvatar;
            this.equipAvatarExcelId = 0; // Legacy field
            return true;
        }
        
        return false;
    }
    
    // Sub affixes
    
    public void resetSubAffixes() {
        if (this.subAffixes != null) {
            this.subAffixes.clear();
        } else {
            this.subAffixes = new ArrayList<>();
        }
    }
    
    public void addSubAffix(GameItemSubAffix subAffix) {
        if (this.subAffixes == null) {
            this.subAffixes = new ArrayList<>();
        }
        
        this.subAffixes.add(subAffix);
    }

    public void addRandomSubAffixes(int quantity) {
        for (int i = 0; i < quantity; i++) {
            this.addRandomSubAffix();
        }
    }

    public void addRandomSubAffix() {
        if (this.subAffixes == null) {
            this.subAffixes = new ArrayList<>();
        }

        if (this.subAffixes.size() < 4) {
            this.addNewRandomSubAffix();
        } else {
            this.upgradeRandomSubAffix();
        }
    }

    private void addNewRandomSubAffix() {
        // Get list of affixes to add
        List<RelicSubAffixExcel> affixList = GameDepot.getRelicSubAffixList(getExcel().getRelicExcel().getSubAffixGroup());
        if (affixList == null) return;

        // Blacklist main affix and any sub affixes
        AvatarPropertyType mainAffixProperty = AvatarPropertyType.Unknown;
        RelicMainAffixExcel mainAffix = GameData.getRelicMainAffixExcelMap().get(getExcel().getRelicExcel().getMainAffixGroup(), this.mainAffix);
        if (mainAffix != null) {
            mainAffixProperty = mainAffix.getProperty();
        }

        IntSet blacklist = new IntOpenHashSet();
        for (GameItemSubAffix subAffix : this.getSubAffixes()) {
            blacklist.add(subAffix.getId());
        }

        // Build random list
        WeightedList<RelicSubAffixExcel> randomList = new WeightedList<>();
        for (RelicSubAffixExcel affix : affixList) {
            if (affix.getProperty() != mainAffixProperty && !blacklist.contains(affix.getAffixID())) {
                randomList.add(10, affix);
            }
        }

        // Sanity check
        if (randomList.size() == 0) {
            return;
        }

        // Add random stat
        RelicSubAffixExcel subAffix = randomList.next();
        this.addSubAffix(new GameItemSubAffix(subAffix));
    }

    public void upgradeRandomSubAffix() {
        this.upgradeRandomSubAffix(this.subAffixes);
    }
    
    private void upgradeRandomSubAffix(List<GameItemSubAffix> subAffixes) {
        GameItemSubAffix subAffix = Utils.randomElement(subAffixes);
        var subAffixExcel = GameData.getRelicSubAffixExcelMap().get(this.getExcel().getRelicExcel().getSubAffixGroup(), subAffix.getId());
        subAffix.incrementCount(subAffixExcel.getStepNum());
    }
    
    /**
     * Returns the amount of sub affixes this item has
     */
    public int getSubAffixListSize() {
        if (this.subAffixes == null) return 0;
        return this.subAffixes.size();
    }
    
    /**
     * Returns the current count of sub affixes this item has
     */
    public int getCurrentSubAffixCount() {
        if (this.subAffixes == null) return 0;
        
        return this.subAffixes
                .stream()
                .reduce(0, (subtotal, subAffix) -> subtotal + subAffix.getCount(), Integer::sum);
    }
    
    /**
     * Returns the maximum amount of sub affixes this item should normally have
     */
    public int getMaxNormalSubAffixCount() {
        return (getExcel().getRarity().getVal() - 1) + (int) Math.floor(this.getLevel() / 3.0);
    }
    
    public void sortSubAffixes() {
        if (this.subAffixes == null || this.subAffixes.size() == 0) {
            return;
        }
        
        Collections.sort(this.subAffixes);
    }

    public void reforgeSubAffixes() {
        this.reforgedSubAffixes = new ArrayList<>();
        
        for (var subAffix : this.getSubAffixes()) {
            @SuppressWarnings("deprecation")
            var newAffix = new GameItemSubAffix();
            
            newAffix.setId(subAffix.getId());
            newAffix.setCount(1);
            newAffix.setStep(Utils.randomRange(0, 2));
            
            this.getReforgedSubAffixes().add(newAffix);
        }
        
        int rerollCount = this.getCurrentSubAffixCount() - this.getSubAffixListSize();
        for (int i = 0; i < rerollCount; i++) {
            this.upgradeRandomSubAffix(this.getReforgedSubAffixes());
        }
    }

    public void confirmReforge(boolean keep) {
        // Sanity
        if (this.reforgedSubAffixes == null) {
            return;
        }
        
        // Replace old affixes if the player confirmed
        if (!keep) {
            this.subAffixes = this.reforgedSubAffixes;
        }
        
        // Clear reforged affixes
        this.reforgedSubAffixes = null;
    }

    // Database

    public void save() {
        if (this.count > 0 && this.ownerUid > 0) {
            LunarCore.getGameDatabase().save(this);
        } else if (this.getId() != null) {
            LunarCore.getGameDatabase().delete(this);
        }
    }
    
    // Player sync
    
    public void onSync(PlayerSyncScNotify proto) {
        switch (this.getExcel().getItemMainType().getTabType()) {
            case MATERIAL -> {
                proto.addMaterialList(this.toMaterialProto());
            }
            case RELIC -> {
                if (this.getCount() > 0) {
                    proto.addRelicList(this.toRelicProto());
                } else {
                    proto.addDelRelicList(this.getInternalUid());
                }
            }
            case EQUIPMENT -> {
                if (this.getCount() > 0) {
                    proto.addEquipmentList(this.toEquipmentProto());
                } else {
                    proto.addDelEquipmentList(this.getInternalUid());
                }
            }
            default -> {
                // Skip
            }
        }
    }

    // Proto

    public Material toMaterialProto() {
        var proto = Material.newInstance()
                .setTid(this.getItemId())
                .setNum(this.getCount());
        
        return proto;
    }

    public Relic toRelicProto() {
        var proto = Relic.newInstance()
                .setTid(this.getItemId())
                .setUniqueId(this.getInternalUid())
                .setLevel(this.getLevel())
                .setExp(this.getExp())
                .setIsProtected(this.isLocked())
                .setIsDiscarded(this.isDiscarded())
                .setMainAffixId(this.mainAffix);
        
        if (this.getEquipAvatar() != null) {
            proto.setEquipAvatarId(this.getEquipAvatar().getExcelId());
        }

        if (this.subAffixes != null) {
            for (var subAffix : this.subAffixes) {
                proto.addSubAffixList(subAffix.toProto());
            }
        }
        
        if (this.reforgedSubAffixes != null) {
            for (var subAffix : this.reforgedSubAffixes) {
                proto.addReforgeSubAffixList(subAffix.toProto());
            }
        }
        
        return proto;
    }

    public Equipment toEquipmentProto() {
        var proto = Equipment.newInstance()
                .setTid(this.getItemId())
                .setUniqueId(this.getInternalUid())
                .setLevel(this.getLevel())
                .setExp(this.getExp())
                .setIsProtected(this.isLocked())
                .setPromotion(this.getPromotion())
                .setRank(this.getRank());
        
        if (this.getEquipAvatar() != null) {
            proto.setEquipAvatarId(this.getEquipAvatar().getExcelId());
        }
        
        return proto;
    }

    public PileItem toPileProto() {
        return PileItem.newInstance()
                .setItemId(this.getItemId())
                .setItemNum(this.getCount());
    }

    public Item toProto() {
        return Item.newInstance()
                .setItemId(this.getItemId())
                .setNum(this.getCount())
                .setLevel(this.getLevel())
                .setMainAffixId(this.getMainAffix())
                .setRank(this.getRank())
                .setPromotion(this.getPromotion())
                .setUniqueId(this.getInternalUid());
    }
}
