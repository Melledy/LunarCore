package emu.lunarcore.data.excel;

import emu.lunarcore.data.GameResource;
import emu.lunarcore.data.ResourceType;
import lombok.Getter;

@Getter
@ResourceType(name = {"RogueAeon.json"})
public class RogueAeonExcel extends GameResource {
    private int AeonID;
    private int RogueBuffType;

    @Override
    public int getId() {
        return AeonID;
    }

}
