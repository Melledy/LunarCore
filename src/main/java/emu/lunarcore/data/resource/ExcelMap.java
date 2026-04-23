package emu.lunarcore.data.resource;

import java.util.Iterator;
import java.util.stream.Stream;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntCollection;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.objects.ObjectCollection;
import it.unimi.dsi.fastutil.objects.ObjectSet;

public class ExcelMap<T extends GameResource> implements Iterable<T>, ResourceMap<T> {
    private Int2ObjectMap<T> map;
    
    public ExcelMap() {
        this.map = new Int2ObjectOpenHashMap<>();
    }
    
    public void add(T excel) {
        this.map.put(excel.getId(), excel);
    }
    
    public int size() {
        return this.map.size();
    }
    
    // Wrapper functions
    
    /**
     * Wrapper for {@link it.unimi.dsi.fastutil.ints.Int2ObjectMap.get}
     */
    public T get(int id) {
        return this.map.get(id);
    }

    /**
     * Wrapper for {@link it.unimi.dsi.fastutil.ints.Int2ObjectMap.containsKey}
     */
    public boolean containsKey(int id) {
        return this.map.containsKey(id);
    }

    /**
     * Wrapper for {@link it.unimi.dsi.fastutil.ints.Int2ObjectMap.keySet}
     */
    public IntSet keySet() {
        return this.map.keySet();
    }

    /**
     * Wrapper for {@link it.unimi.dsi.fastutil.ints.Int2ObjectMap.values}
     */
    public ObjectCollection<T> values() {
        return this.map.values();
    }

    /**
     * Wrapper for {@link it.unimi.dsi.fastutil.ints.Int2ObjectMap.int2ObjectEntrySet}
     */
    public ObjectSet<Int2ObjectMap.Entry<T>> int2ObjectEntrySet() {
        return this.map.int2ObjectEntrySet();
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
        return this.map.keySet();
    }
    
    public IntCollection getAllIds() {
        return this.getIds();
    }

}
