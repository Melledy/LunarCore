package emu.lunarcore.data.excel;

import emu.lunarcore.data.GameResource;
import emu.lunarcore.data.ResourceType;
import emu.lunarcore.data.ResourceType.LoadPriority;
import lombok.Getter;

@Getter
@ResourceType(name = {"PlayerIcon.json"}, loadPriority = LoadPriority.NORMAL)
public class PlayerIconExcel extends GameResource {
    private int ID;

    @Override
    public int getId() {
        return ID;
    }
}
