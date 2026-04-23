package emu.lunarcore.data.excel;

import emu.lunarcore.data.resource.GameResource;
import emu.lunarcore.data.resource.ResourceType;
import lombok.Getter;

@Getter
@ResourceType(name = {"PetConfig.json"})
public class PetExcel extends GameResource {
    private int PetID;
    private int PetItemID;
    private int SummonUnitID;

    @Override
    public int getId() {
        return PetID;
    }

}
