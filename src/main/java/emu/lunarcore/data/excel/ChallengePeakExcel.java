package emu.lunarcore.data.excel;

import emu.lunarcore.data.resource.GameResource;
import emu.lunarcore.data.resource.ResourceType;
import lombok.Getter;

@Getter
@ResourceType(name = {"ChallengePeakConfig.json"})
public class ChallengePeakExcel extends GameResource {
    private int ID;
    private int[] EventIDList;

    @Override
    public int getId() {
        return ID;
    }
}
