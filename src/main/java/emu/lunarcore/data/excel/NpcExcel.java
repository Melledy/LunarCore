package emu.lunarcore.data.excel;

import emu.lunarcore.data.resource.GameResource;
import emu.lunarcore.data.resource.ResourceType;
import lombok.Getter;

@Getter
@ResourceType(name = {"NPCData.json"})
public class NpcExcel extends GameResource {
    private int ID;

    @Override
    public int getId() {
        return ID;
    }

}
