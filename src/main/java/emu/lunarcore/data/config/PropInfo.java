package emu.lunarcore.data.config;

import emu.lunarcore.game.enums.PropState;
import emu.lunarcore.game.scene.triggers.PropTrigger;
import lombok.Getter;
import lombok.Setter;

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
    private int ChestID;
    
    @Setter private String InitLevelGraph;
    @Setter private PropState State = PropState.Closed;
    
    @Setter private transient PropTrigger trigger;
}
