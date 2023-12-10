package emu.lunarcore.game.drops;

import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;

public class DropMap extends Int2IntOpenHashMap {
    private static final long serialVersionUID = -4186524272780523459L;

    public FastEntrySet entries() {
        return this.int2IntEntrySet();
    }
}
