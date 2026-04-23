package emu.lunarcore.data.excel;

import emu.lunarcore.data.resource.GameResource;
import emu.lunarcore.data.resource.ResourceType;
import emu.lunarcore.game.enums.PropState;
import lombok.Getter;

@Getter
@ResourceType(name = {"InteractConfig.json"})
public class InteractExcel extends GameResource {
    private int InteractID;
    private PropState SrcState = PropState.Closed;
    private PropState TargetState = PropState.Closed;

    @Override
    public int getId() {
        return InteractID;
    }

    @Override
    public void onLoad() {
        // Just in case we forget to update the prop state enum
        if (this.SrcState == null) {
            this.SrcState = PropState.Closed;
        }
        if (this.TargetState == null) {
            this.TargetState = PropState.Closed;
        }
    }
}
