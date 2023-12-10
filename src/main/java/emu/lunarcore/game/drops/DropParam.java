package emu.lunarcore.game.drops;

import emu.lunarcore.data.GameData;
import emu.lunarcore.data.excel.ItemExcel;
import emu.lunarcore.util.Utils;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class DropParam {
    @Setter(AccessLevel.NONE) 
    private IntList items;
    
    private int minCount;
    private int maxCount;
    private int chance;
    
    public DropParam() {
        this.items = new IntArrayList();
        this.chance = 1000;
    }
    
    public DropParam(int itemId, int count) {
        this();
        this.getItems().add(itemId);
        this.setCount(count);
    }
    
    public DropParam(int itemId, double count) {
        this();
        this.getItems().add(itemId);
        this.setCount(count);
    }
    
    public void setCount(int count) {
        this.minCount = count;
        this.maxCount = count;
    }
    
    public void setCount(double count) {
        if (count % 1 == 0) {
            this.setCount((int) count);
        } else {
            this.setMaxCount((int) Math.ceil(count));
            this.setMinCount((int) Math.floor(count));
        }
    }
    
    public int generateItemId() {
        if (this.items == null || this.items.size() == 0) {
            return 0;
        }
        
        if (this.items.size() == 1) {
            return this.items.getInt(0);
        }
        
        return Utils.randomElement(this.items); 
    }
    
    public int generateCount() {
        if (this.maxCount > this.minCount) {
            return Utils.randomRange(this.minCount, this.maxCount);
        }
        return this.maxCount;
    }
    
    public void roll(DropMap drops) {
        // Check drop chance
        if (this.chance < 1000) {
            int random = Utils.randomRange(0, 999);
            if (random > this.chance) {
                return;
            }
        }
        
        // Get count
        int count = generateCount();

        // Generate item(s)
        while (count > 0) {
            int itemId = generateItemId();
            
            ItemExcel excel = GameData.getItemExcelMap().get(itemId);
            if (excel == null) break;
         
            if (excel.isEquippable()) {
                drops.addTo(itemId, 1);
                count--;
            } else {
                drops.addTo(itemId, count);
                count -= count;
            }
        }
    }
}
