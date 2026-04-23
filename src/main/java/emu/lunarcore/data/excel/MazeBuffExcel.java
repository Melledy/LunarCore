package emu.lunarcore.data.excel;

import emu.lunarcore.data.resource.MultiKeyGameResource;
import emu.lunarcore.data.resource.ResourceType;
import lombok.Getter;

@Getter
@ResourceType(name = {"MazeBuff.json"})
public class MazeBuffExcel extends MultiKeyGameResource {
    private int ID;
    private int Lv;

    @Override
    public int getPrimaryKey() {
        return ID;
    }

    @Override
    public int getSecondaryKey() {
        return Lv;
    }

    public int getBuffId() {
        return ID;
    }
}
