package emu.lunarcore.data.config;

import emu.lunarcore.game.enums.PropState;
import lombok.Getter;

@Getter
public class PropInfo extends ObjectInfo {
    public float RotX;
    public float RotZ;
    private int MappingInfoID;
    private int AnchorGroupID;
    private int AnchorID;
    private int PropID;
    private int EventID;
    private PropState State = PropState.Closed;
    private boolean IsDelete;
}
