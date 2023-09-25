package emu.lunarcore.data.excel;

import emu.lunarcore.data.GameResource;
import emu.lunarcore.data.ResourceType;
import emu.lunarcore.data.ResourceType.LoadPriority;
import lombok.Getter;

@Getter
@ResourceType(name = {"ExpType.json"}, loadPriority = LoadPriority.NORMAL)
public class ExpTypeExcel extends GameResource {
    private int TypeID;
    private int Level;
    private int Exp;

    @Override
    public int getId() {
        return (TypeID << 16) + Level;
    }

    @Override
    public void onLoad() {

    }
}
