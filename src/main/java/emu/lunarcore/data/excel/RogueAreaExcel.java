package emu.lunarcore.data.excel;

import emu.lunarcore.data.GameResource;
import emu.lunarcore.data.ResourceType;
import lombok.Getter;

@Getter
@ResourceType(name = {"RogueAreaConfig.json"})
public class RogueAreaExcel extends GameResource {
    private int RogueAreaID;
    private int AreaProgress;
    private int Difficulty;
    
    @Override
    public int getId() {
        return RogueAreaID;
    }

}
