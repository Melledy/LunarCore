package emu.lunarcore.game.inventory;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import emu.lunarcore.data.GameData;
import emu.lunarcore.data.common.ItemParam;
import emu.lunarcore.data.excel.ItemExcel;
import it.unimi.dsi.fastutil.ints.Int2IntLinkedOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterable;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;

public class ItemParamMap extends Int2IntLinkedOpenHashMap implements ObjectBidirectionalIterable<Int2IntMap.Entry> {
    private static final long serialVersionUID = -4186524272780523459L;
    
    public static final ItemParamMap EMPTY = new ItemParamMap() {
        public int put(int itemId, int count) {
            return 0;
        }
        
        public int add(int itemId, int count) {
            return 0;
        }
    };
    
    public ItemParamMap() {
        super();
    }
    
    public ItemParamMap(List<ItemParam> list) {
        super();
        
        for (var param : list) {
            this.add(param.getId(), param.getCount());
        }
    }
    
    @Override @Deprecated
    public int addTo(int itemId, int count) {
        return this.add(itemId, count);
    }
    
    public int add(int itemId, int count) {
        if (count == 0) {
            return 0;
        }
        
        return super.addTo(itemId, count);
    }

    public void add(List<ItemParam> list) {
        for (var param : list) {
            this.add(param.getId(), param.getCount());
        }
    }
    
    /**
     * Adds all item params from the other map to this one
     * @param map The other item param map
     */
    public void add(ItemParamMap map) {
        for (var entry : map.entries()) {
            this.add(entry.getIntKey(), entry.getIntValue());
        }
    }
    
    /**
     * Returns a new ItemParamMap with item amounts multiplied
     * @param mult Value to multiply all item amounts in this map by
     * @return
     */
    public ItemParamMap mulitply(int multiplier) {
        var params = new ItemParamMap();
        
        for (var entry : this.int2IntEntrySet()) {
            params.put(entry.getIntKey(), entry.getIntValue() * multiplier);
        }
        
        return params;
    }
    

    // Iterable

    @Override
    public ObjectBidirectionalIterator<Int2IntMap.Entry> iterator() {
        return this.int2IntEntrySet().iterator();
    }
    
    public FastEntrySet entries() {
        return this.int2IntEntrySet();
    }
    
    // Helpers
    
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

    public List<ItemParam> toItemParamList() {
        List<ItemParam> list = new ArrayList<>();
        
        for (var entry : this) {
            int itemId = entry.getIntKey();
            int count = entry.getIntValue();
            
            list.add(new ItemParam(itemId, count));
        }
        
        return list;
    }
}
