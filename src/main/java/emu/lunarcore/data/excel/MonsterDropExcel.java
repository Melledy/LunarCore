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
import lombok.AccessLevel;
import lombok.Getter;

@Getter
@ResourceType(name = {"MonsterDrop.json"}, loadPriority = LoadPriority.LOW)
public class MonsterDropExcel extends GameResource {
    private int MonsterTemplateID;
    private int WorldLevel;
    private int AvatarExpReward;

    @Getter(AccessLevel.PRIVATE)
    private List<ItemParam> DisplayItemList;
    
    // Temp solution for handling drop tables
    private transient List<DropParam> dropList;

    @Override
    public int getId() {
        return (MonsterTemplateID << 4) + WorldLevel;
    }

    @Override
    public void onLoad() {
        // Temp way to pre-calculate drop list
        if (this.getDisplayItemList() == null || this.getDisplayItemList().size() == 0) {
            this.dropList = new ArrayList<>(0);
            return;
        }
        
        this.dropList = new ArrayList<>(this.getDisplayItemList().size());
        
        for (var itemParam : this.getDisplayItemList()) {
            // Add item param if the amount is already set in the excel
            if (itemParam.getCount() > 0) {
                dropList.add(new DropParam(itemParam.getId(), itemParam.getCount()));
                continue;
            }
            
            // TODO drop rate is not correct
            if (itemParam.getId() == GameConstants.MATERIAL_COIN_ID) {
                dropList.add(new DropParam(itemParam.getId(), getAvatarExpReward()));
                continue;
            }
            
            // Get item excel
            ItemExcel itemExcel = GameData.getItemExcelMap().get(itemParam.getId());
            if (itemExcel == null) continue;
            
            // TODO drop rate is not correct
            double mod = switch (itemExcel.getRarity()) {
                case NotNormal -> 0.8;
                case Rare -> 0.3;
                case VeryRare -> 0.125;
                case SuperRare -> 0;
                default -> 1.0;
            };
            
            double baseAmount = this.getWorldLevel() + 3;
            
            // Create drop param
            var drop = new DropParam(itemParam.getId(), 1);
            drop.setMaxCount((int) Math.ceil(baseAmount * mod));
            drop.setMinCount((int) Math.floor(baseAmount * mod * 0.5));
            
            if (drop.getMaxCount() > 0) {
                dropList.add(drop);
            }
        }
        
        // Clear list once were done with it to free some memory
        this.DisplayItemList = null;
    }
}
