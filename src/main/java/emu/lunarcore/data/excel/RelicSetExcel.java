package emu.lunarcore.data.excel;

import emu.lunarcore.data.resource.GameResource;
import emu.lunarcore.data.resource.ResourceType;
import emu.lunarcore.data.resource.ResourceType.LoadPriority;
import lombok.Getter;

@Getter
@ResourceType(name = {"RelicSetConfig.json"}, loadPriority = LoadPriority.NORMAL)
public class RelicSetExcel extends GameResource {
    private int SetID;

    @Override
    public int getId() {
        return SetID;
    }
}
