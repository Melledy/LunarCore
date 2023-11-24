package emu.lunarcore.data.config;

import java.util.List;

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
    private PropValueSource ValueSource;
    
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

        var value = getValueSource().getValues().stream().filter(v -> key.equals(v.Key)).findFirst().orElse(null);
        
        if (value != null) {
            return value.getValue();
        }
        
        return null;
    }
    
    @Getter
    public static class PropValueSource {
        private List<PropSharedValue> Values;
    }
    
    @Getter
    public static class PropSharedValue {
        private String Key;
        private String Value;
    }
}
