package emu.lunarcore.game.inventory;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import emu.lunarcore.data.GameData;
import emu.lunarcore.data.excel.ItemExcel;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;

public class ItemParamMap extends Int2IntOpenHashMap {
    private static final long serialVersionUID = -4186524272780523459L;
    
    @Override
    public int addTo(int itemId, int count) {
        return super.addTo(itemId, count);
    }

    public FastEntrySet entries() {
        return this.int2IntEntrySet();
    }
    
    public void forEachItem(Consumer<GameItem> consumer) {
        for (var entry : this.entries()) {
            // Get amount
            int amount = entry.getIntValue();
            if (amount <= 0) {
                continue;
            }
            
            // Create item and add it to player
            ItemExcel excel = GameData.getItemExcelMap().get(entry.getIntKey());
            if (excel == null) continue;
            
            // Add item
            if (excel.isEquippable()) {
                for (int i = 0; i < amount; i++) {
                    consumer.accept(new GameItem(excel, 1));
                }
            } else {
                consumer.accept(new GameItem(excel, amount));
            }
        }
    }
    
    public List<GameItem> toItemList() {
        List<GameItem> list = new ArrayList<>();
        
        this.forEachItem(item -> {
            list.add(item);
        });
        
        return list;
    }
}
