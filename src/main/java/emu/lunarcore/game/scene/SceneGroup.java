package emu.lunarcore.game.scene;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import lombok.Getter;

@Getter
public class SceneGroup {
    private int id;
    private int state;
    private Object2IntMap<String> properties;
    
    public SceneGroup(int id) {
        this.id = id;
        this.properties = new Object2IntOpenHashMap<>();
    }
}
