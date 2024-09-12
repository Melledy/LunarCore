package emu.lunarcore.data.excel;

import emu.lunarcore.data.GameResource;
import emu.lunarcore.data.ResourceType;
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
