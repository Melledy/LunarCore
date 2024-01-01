package emu.lunarcore.data.excel;

import emu.lunarcore.data.GameResource;
import emu.lunarcore.data.ResourceType;
import emu.lunarcore.game.enums.PropState;
import lombok.Getter;

@Getter
@ResourceType(name = {"InteractConfig.json"})
public class InteractExcel extends GameResource {
    private int InteractID;
    private PropState SrcState;
    private PropState TargetState = PropState.Closed;

    @Override
    public int getId() {
        return InteractID;
    }

    @Override
    public void onLoad() {
        // Just in case we forget to update the prop state enum
        if (this.TargetState == null) {
            this.TargetState = PropState.Closed;
        }
    }
}
