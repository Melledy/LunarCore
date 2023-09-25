package emu.lunarcore.data.excel;

import emu.lunarcore.data.GameResource;
import emu.lunarcore.data.ResourceType;
import emu.lunarcore.data.ResourceType.LoadPriority;
import lombok.Getter;

@Getter
@ResourceType(name = {"PlayerLevelConfig.json"}, loadPriority = LoadPriority.NORMAL)
public class PlayerLevelExcel extends GameResource {
    private int Level;
    private int PlayerExp;

    @Override
    public int getId() {
        return Level;
    }
}
