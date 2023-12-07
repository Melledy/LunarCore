package emu.lunarcore.data.excel;

import java.util.List;

import emu.lunarcore.data.GameResource;
import emu.lunarcore.data.ResourceType;
import emu.lunarcore.data.common.ItemParam;
import emu.lunarcore.game.enums.ItemMainType;
import emu.lunarcore.game.enums.ItemRarity;
import emu.lunarcore.game.enums.ItemSubType;
import emu.lunarcore.game.enums.ItemUseMethod;
import lombok.Getter;
import lombok.Setter;

@Getter
@ResourceType(name = {"ItemConfig.json", "ItemConfigAvatar.json", "ItemConfigAvatarPlayerIcon.json", "ItemConfigAvatarRank.json",
        "ItemConfigBook.json", "ItemConfigDisk.json", "ItemConfigEquipment.json", "ItemConfigRelic.json", "ItemPlayerCard.json"})
public class ItemExcel extends GameResource {
    // General item data
    private int ID;
    private long ItemName;
    private ItemMainType ItemMainType = emu.lunarcore.game.enums.ItemMainType.Unknown;
    private ItemSubType ItemSubType = emu.lunarcore.game.enums.ItemSubType.Unknown;
    private ItemRarity Rarity;
    private int PileLimit;
    private int PurposeType;
    
    private int UseDataID;
    private ItemUseMethod UseMethod;

    private List<ItemParam> ReturnItemIDList;

    // Transient cache
    @Setter private transient EquipmentExcel equipmentExcel;
    @Setter private transient RelicExcel relicExcel;

    @Setter private transient int avatarExp;
    @Setter private transient int relicExp;
    @Setter private transient int equipmentExp;
    @Setter private transient int expCost;

    @Override
    public int getId() {
        return ID;
    }

    public boolean isEquipment() {
        return ItemMainType == emu.lunarcore.game.enums.ItemMainType.Equipment && this.getEquipmentExcel() != null;
    }

    public boolean isHeadIcon() {
        return ItemSubType == emu.lunarcore.game.enums.ItemSubType.HeadIcon;
    }

    public boolean isRelic() {
        return ItemMainType == emu.lunarcore.game.enums.ItemMainType.Relic && this.getRelicExcel() != null;
    }

    public boolean isEquippable() {
        return ItemMainType == emu.lunarcore.game.enums.ItemMainType.Relic || ItemMainType == emu.lunarcore.game.enums.ItemMainType.Equipment;
    }
    
    public int getRarityNum() {
        return this.getRarity().getVal();
    }

    public int getRelicExp() {
        if (this.relicExcel != null) {
            return this.relicExcel.getExpProvide();
        }
        return this.relicExp;
    }
    
    public int getRelicExpCost() {
        if (this.relicExcel != null) {
            return this.relicExcel.getCoinCost();
        }
        return this.expCost;
    }

    public int getEquipmentExp() {
        if (this.equipmentExcel != null) {
            return this.equipmentExcel.getExpProvide();
        }
        return this.equipmentExp;
    }

    public int getEquipmentExpCost() {
        if (this.equipmentExcel != null) {
            return this.equipmentExcel.getCoinCost();
        }
        return this.expCost;
    }

    public int getEquipSlot() {
        if (this.getRelicExcel() != null) {
            return this.getRelicExcel().getType().getVal();
        } else if (this.getEquipmentExcel() != null) {
            return 100;
        }
        return 0;
    }
}
