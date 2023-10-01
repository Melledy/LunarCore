package emu.lunarcore.data.config;

import emu.lunarcore.game.enums.PropState;
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
    
    @Setter
    private PropState State = PropState.Closed;
}
