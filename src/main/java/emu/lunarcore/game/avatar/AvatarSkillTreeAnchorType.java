package emu.lunarcore.game.avatar;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import lombok.Getter;

@Getter
public enum AvatarSkillTreeAnchorType {
    None (0),
    Point01 (1),
    Point02 (2),
    Point03 (3),
    Point04 (4),
    Point05 (5),
    Point06 (6),
    Point07 (7),
    Point08 (8),
    Point09 (9),
    Point10 (10),
    Point11 (11),
    Point12 (12),
    Point13 (13),
    Point14 (14),
    Point15 (15),
    Point16 (16),
    Point17 (17),
    Point18 (18),
    Point19 (19),
    Point20 (20),
    Point21 (21),
    Point22 (22);
    
    private int value;
    private static Object2IntMap<String> map = new Object2IntOpenHashMap<>();
    
    static {
        for (var point : values()) {
            map.put(point.name(), point.getValue());
        }
    }
    
    private AvatarSkillTreeAnchorType(int value) {
        this.value = value;
    }
    
    public static int getValue(String name) {
        return map.getInt(name);
    }
}
