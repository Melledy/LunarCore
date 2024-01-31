package emu.lunarcore.game.rogue;

import java.util.Arrays;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import lombok.Getter;
import lombok.Setter;

@Getter
public enum RogueBuffType {
    Default         (100),
    Preservation    (120),
    Remembrance     (121),
    Nihility        (122),
    Abundance       (123),
    TheHunt         (124),
    Destruction     (125),
    Elation         (126),
    Propagation     (127),
    Erudition       (128);
    
    private final int val;
    @Setter private int battleEventSkill;
    
    private static final Int2ObjectMap<RogueBuffType> map = new Int2ObjectOpenHashMap<>();
    
    static {
        Arrays.stream(values()).forEach(type -> map.put(type.getVal(), type));
    }
    
    private RogueBuffType(int val) {
        this.val = val;
    }
    
    public static RogueBuffType getById(int id) {
        return map.get(id);
    }
}
