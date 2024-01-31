package emu.lunarcore.data.excel;

import emu.lunarcore.data.GameDepot;
import emu.lunarcore.data.GameResource;
import emu.lunarcore.data.ResourceType;
import lombok.Getter;

@Getter
@ResourceType(name = {"RogueMiracle.json"})
public class RogueMiracleExcel extends GameResource {
    private int MiracleID;
    private boolean IsShow;
    private int MiracleReward;

    @Override
    public int getId() {
        return MiracleID;
    }

    @Override
    public void onLoad() {
        //if (IsShow && MiracleReward > 0) {   // it is always false
            GameDepot.getRogueRandomMiracleList().add(this);
        //}
    }
}
