package emu.lunarcore.data.resource;

import java.util.Iterator;
import java.util.stream.Stream;

import it.unimi.dsi.fastutil.ints.*;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectCollection;

public class MultiKeyExcelMap<T extends MultiKeyGameResource> implements Iterable<T>, ResourceMap<T> {
    private Long2ObjectMap<T> map;
    
    public MultiKeyExcelMap() {
        this.map = new Long2ObjectOpenHashMap<>();
    }
    
    public void add(T excel) {
        long key = ((long) excel.getPrimaryKey() << 32) + (long) excel.getSecondaryKey();
        this.map.put(key, excel);
    }
    
    public int size() {
        return this.map.size();
    }
    
    // Wrapper functions
    
    /**
     * Wrapper for {@link it.unimi.dsi.fastutil.ints.Int2ObjectMap.get}
     */
    public T get(int primaryKey, int secondaryKey) {
        long key = ((long) primaryKey << 32) + (long) secondaryKey;
        return this.map.get(key);
    }
    
    /**
     * Wrapper for {@link it.unimi.dsi.fastutil.ints.Int2ObjectMap.containsKey}
     */
    public boolean containsKey(int id) {
        return this.map.containsKey(id);
    }

    /**
     * Wrapper for {@link it.unimi.dsi.fastutil.ints.Int2ObjectMap.values}
     */
    public ObjectCollection<T> values() {
        return this.map.values();
    }
    
    // Iterable/Streamable
    
    @Override
    public Iterator<T> iterator() {
        return this.values().iterator();
    }
    
    public Stream<T> stream() {
        return this.values().stream();
    }
    
    // Custom
    
    public IntCollection getIds() {
        var set = new IntOpenHashSet();
        this.map.values().stream().mapToInt(MultiKeyGameResource::getPrimaryKey).forEach(set::add);
        return set;
    }

}
