package emu.lunarcore.data.excel;

import emu.lunarcore.data.resource.GameResource;
import emu.lunarcore.data.resource.ResourceType;
import lombok.Getter;

@Getter
@ResourceType(name = {"OfferingTypeConfig.json"})
public class OfferingTypeExcel extends GameResource {
    private int ID;
    private int MaxLevel;

    @Override
    public int getId() {
        return ID;
    }
}
