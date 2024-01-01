package emu.lunarcore.data.config;

import java.util.List;

import com.google.gson.JsonObject;

import emu.lunarcore.game.enums.PropState;
import emu.lunarcore.game.scene.triggers.PropTrigger;
import emu.lunarcore.util.Position;
import lombok.Getter;
import lombok.Setter;

/**
 *  Original name: LevelPropInfo
 */
@Getter
public class PropInfo extends ObjectInfo {
    private float RotX;
    private float RotZ;
    private int MappingInfoID;
    private int AnchorGroupID;
    private int AnchorID;
    private int PropID;
    private int EventID;
    private int CocoonID;
    private int FarmElementID;
    private boolean IsClientOnly;
    
    @Setter private PropValueSource ValueSource;
    @Setter private String InitLevelGraph;
    @Setter private PropState State = PropState.Closed;
    
    @Setter private transient PropTrigger trigger;
    
    @Override
    public Position getRot() {
        if (this.rot == null) {
            this.rot = new Position((int) (this.RotX * 1000f), (int) (this.RotY * 1000f), (int) (this.RotZ * 1000f));
        }
        return this.rot;
    }
    
    public String getSharedValueByKey(String key) {
        if (this.getValueSource() == null) return null;

        var sharedValue = getValueSource().getValues()
                .stream()
                .filter(v -> v.has("Key") && v.get("Key").getAsString().equals(key))
                .findFirst()
                .orElse(null);
        
        if (sharedValue != null && sharedValue.has("Value")) {
            return sharedValue.get("Value").getAsString();
        }
        
        return null;
    }
    
    @Getter
    public static class PropValueSource {
        private List<JsonObject> Values;
    }
}
