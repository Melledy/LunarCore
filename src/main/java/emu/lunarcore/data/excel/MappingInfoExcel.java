package emu.lunarcore.data.excel;

import java.util.ArrayList;
import java.util.List;

import emu.lunarcore.GameConstants;
import emu.lunarcore.data.GameData;
import emu.lunarcore.data.GameResource;
import emu.lunarcore.data.ResourceType;
import emu.lunarcore.data.ResourceType.LoadPriority;
import emu.lunarcore.data.common.ItemParam;
import emu.lunarcore.game.drops.DropParam;
import emu.lunarcore.game.enums.ItemMainType;
import emu.lunarcore.game.enums.ItemRarity;
import emu.lunarcore.game.enums.ItemSubType;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import lombok.Getter;

@Getter
@ResourceType(name = {"MappingInfo.json"}, loadPriority = LoadPriority.LOW)
public class MappingInfoExcel extends GameResource {
    private int ID;
    private int WorldLevel;
    private String FarmType; // is enum
    private List<ItemParam> DisplayItemList;
    
    private transient List<DropParam> dropList;
    
    @Override
    public int getId() {
        return (ID << 8) + WorldLevel;
    }

    @Override
    public void onLoad() {
        // Temp way to pre-calculate drop list
        this.dropList = new ArrayList<>(this.getDisplayItemList().size());
        
        var equipmentDrops = new IntArrayList();
        var relicDrops = new Int2ObjectOpenHashMap<IntList>();
        
        for (var itemParam : this.getDisplayItemList()) {
            // Add item param if the amount is already set in the excel
            if (itemParam.getCount() > 0) {
                dropList.add(new DropParam(itemParam.getId(), itemParam.getCount()));
                continue;
            }
            
            // Multiplier. TODO drop rate is not correct
            int multiplier = 1;
            if (FarmType == null) {
                // Skip
            } else if (FarmType.equals("RELIC")) {
                multiplier = 4;
            } else if (FarmType.equals("COCOON2")) {
                multiplier = 3;
            } else if (FarmType.equals("ELEMENT")) {
                multiplier = 3;
            }
            
            // Random credits
            if (itemParam.getId() == GameConstants.MATERIAL_COIN_ID) {
                // TODO drop rate is not correct
                DropParam drop = new DropParam(itemParam.getId(), 0);
                drop.setMinCount((50 + (this.getWorldLevel() * 10)) * multiplier);
                drop.setMaxCount((100 + (this.getWorldLevel() * 10)) * multiplier);
                dropList.add(drop);
                continue;
            }
            
            // Get item excel
            ItemExcel itemExcel = GameData.getItemExcelMap().get(itemParam.getId());
            if (itemExcel == null) continue;
            
            // Hacky way of calculating drops
            if (itemExcel.getItemSubType() == ItemSubType.RelicSetShowOnly) {
                // Get relic base id from relic display id
                int baseRelicId = (itemParam.getId() / 10) % 1000;
                int baseRarity = itemParam.getId() % 10;
                
                // Add relics from the set
                int relicStart = 20001 + (baseRarity * 10000) + (baseRelicId * 10);
                int relicEnd = relicStart + 3;
                for (;relicStart < relicEnd; relicStart++) {
                    ItemExcel relicExcel = GameData.getItemExcelMap().get(relicStart);
                    if (relicExcel == null) break;
                    
                    relicDrops
                        .computeIfAbsent(baseRarity, r -> new IntArrayList())
                        .add(relicStart);
                }
            } else if (itemExcel.getItemMainType() == ItemMainType.Material) {
                // Calculate amount to drop by purpose level
                DropParam drop = switch (itemExcel.getPurposeType()) {
                    // Avatar exp. TODO drop rate is not correct
                    case 1 -> new DropParam(itemParam.getId(), 1);
                    // Boss materials
                    case 2 -> new DropParam(itemParam.getId(), this.getWorldLevel());
                    // Trace materials. TODO drop rate is not correct
                    case 3 -> {
                        var dropInfo = new DropParam(itemParam.getId(), 1);
                        
                        if (itemExcel.getRarity() == ItemRarity.VeryRare) {
                            dropInfo.setChance((this.getWorldLevel() - 3) * 75);
                        }
                        
                        yield dropInfo;
                    }
                    // Boss Trace materials. TODO drop rate is not correct
                    case 4 -> new DropParam(itemParam.getId(), (this.getWorldLevel() * 0.5) + 0.5);
                    // Lightcone exp. TODO drop rate is not correct
                    case 5 -> new DropParam(itemParam.getId(), 1);
                    // Lucent afterglow
                    case 11 -> new DropParam(itemParam.getId(), 4 + this.getWorldLevel());
                    // Unknown
                    default -> null;
                };
                
                if (drop != null) {
                    dropList.add(drop);
                }
            } else if (itemExcel.getItemMainType() == ItemMainType.Equipment) {
                // Lightcones
                equipmentDrops.add(itemParam.getId());
            }
        }
        
        // Add equipment drops
        if (equipmentDrops.size() > 0) {
            DropParam drop = new DropParam();
            drop.getItems().addAll(equipmentDrops);
            drop.setCount(1);
            drop.setChance((this.getWorldLevel() * 10) + 40);
            dropList.add(drop);
        }
        
        // Add relic drops
        if (relicDrops.size() > 0) {
            for (var entry : relicDrops.int2ObjectEntrySet()) {
                // Add items to drop param
                DropParam drop = new DropParam();
                drop.getItems().addAll(entry.getValue());
                
                // Set count by rarity
                double amount = switch (entry.getIntKey()) {
                case 4:
                    yield (this.getWorldLevel() * 0.5) - 0.5;
                case 3:
                    yield (this.getWorldLevel() * 0.5) + (this.getWorldLevel() == 2 ? 1.0 : 0);
                case 2:
                    yield (6 - this.getWorldLevel()) + 0.5 - (this.getWorldLevel() == 1 ? 3.75 : 0);
                default:
                    yield this.getWorldLevel() == 1 ? 6 : 2;
                };
                
                // Set amount
                if (amount > 0) {
                    drop.setCount(amount);
                    dropList.add(drop);
                }
            }
        }
    }
}
