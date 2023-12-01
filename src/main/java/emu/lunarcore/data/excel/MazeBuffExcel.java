package emu.lunarcore.data.excel;

import emu.lunarcore.data.GameResource;
import emu.lunarcore.data.ResourceType;
import lombok.Getter;

@Getter
@ResourceType(name = {"MazeBuff.json"})
public class MazeBuffExcel extends GameResource {
    private int ID;
    private int Lv;

    @Override
    public int getId() {
        return (ID << 4) + Lv;
    }

    public int getBuffId() {
        return ID;
    }
}
