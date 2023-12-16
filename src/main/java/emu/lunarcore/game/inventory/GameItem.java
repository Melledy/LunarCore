package emu.lunarcore.game.inventory;

import java.util.ArrayList;
import java.util.List;

import org.bson.types.ObjectId;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import dev.morphia.annotations.Indexed;
import emu.lunarcore.LunarCore;
import emu.lunarcore.data.GameData;
import emu.lunarcore.data.GameDepot;
import emu.lunarcore.data.excel.ItemExcel;
import emu.lunarcore.data.excel.RelicMainAffixExcel;
import emu.lunarcore.data.excel.RelicSubAffixExcel;
import emu.lunarcore.game.enums.AvatarPropertyType;
import emu.lunarcore.game.enums.ItemMainType;
import emu.lunarcore.game.player.Player;
import emu.lunarcore.proto.EquipmentOuterClass.Equipment;
import emu.lunarcore.proto.ItemOuterClass.Item;
import emu.lunarcore.proto.MaterialOuterClass.Material;
import emu.lunarcore.proto.PileItemOuterClass.PileItem;
import emu.lunarcore.proto.RelicOuterClass.Relic;
import emu.lunarcore.util.Utils;
import emu.lunarcore.util.WeightedList;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import lombok.Getter;
import lombok.Setter;

@Getter
@Entity(value = "items", useDiscriminator = false)
public class GameItem {
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

    @Setter private int mainAffix;
    private List<GameItemSubAffix> subAffixes;

    private int equipAvatar;

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
        this(excel, count, 0);
    }

    public GameItem(ItemExcel excel, int count, int overrideMainAffix) {
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
                if (overrideMainAffix > 0) {
                    this.mainAffix = overrideMainAffix;
                } else {
                    var affix = GameDepot.getRandomRelicMainAffix(getExcel().getRelicExcel().getMainAffixGroup());
                    if (affix != null) {
                        this.mainAffix = affix.getAffixID();
                    }
                }
                // Sub affixes
                int baseSubAffixes = Math.min(Math.max(getExcel().getRarity().getVal() - 2, 0), 3);
                this.addSubAffixes(Utils.randomRange(baseSubAffixes, baseSubAffixes + 1));
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
        return this.getEquipAvatar() > 0;
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

    public boolean setEquipAvatar(int newEquipAvatar) {
        if (this.equipAvatar != newEquipAvatar) {
            this.equipAvatar = newEquipAvatar;
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

    public void addSubAffixes(int quantity) {
        for (int i = 0; i < quantity; i++) {
            this.addSubAffix();
        }
    }

    public void addSubAffix() {
        if (this.subAffixes == null) {
            this.subAffixes = new ArrayList<>();
        }

        if (this.subAffixes.size() < 4) {
            this.addNewSubAffix();
        } else {
            this.upgradeRandomSubAffix();
        }
    }

    private void addNewSubAffix() {
        // Get list of affixes to add
        List<RelicSubAffixExcel> affixList = GameDepot.getRelicSubAffixList(getExcel().getRelicExcel().getSubAffixGroup());
        if (affixList == null) return;

        // Blacklist main affix and any sub affixes
        AvatarPropertyType mainAffixProperty = AvatarPropertyType.Unknown;
        RelicMainAffixExcel mainAffix = GameData.getRelicMainAffixExcel(getExcel().getRelicExcel().getMainAffixGroup(), this.mainAffix);
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
        this.subAffixes.add(new GameItemSubAffix(subAffix));
    }

    private void upgradeRandomSubAffix() {
        GameItemSubAffix subAffix = Utils.randomElement(this.subAffixes);
        var subAffixExcel = GameData.getRelicSubAffixExcel(this.getExcel().getRelicExcel().getSubAffixGroup(), subAffix.getId());
        subAffix.incrementCount(subAffixExcel.getStepNum());
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

    // Database

    public void save() {
        if (this.count > 0 && this.ownerUid > 0) {
            LunarCore.getGameDatabase().save(this);
        } else if (this.getId() != null) {
            LunarCore.getGameDatabase().delete(this);
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
                .setBaseAvatarId(this.getEquipAvatar())
                .setMainAffixId(this.mainAffix);

        if (this.subAffixes != null) {
            for (var subAffix : this.subAffixes) {
                proto.addSubAffixList(subAffix.toProto());
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
                .setRank(this.getRank())
                .setBaseAvatarId(this.getEquipAvatar());
        
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
