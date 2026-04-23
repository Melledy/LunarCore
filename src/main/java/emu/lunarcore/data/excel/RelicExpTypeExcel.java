package emu.lunarcore.data.excel;

import emu.lunarcore.data.resource.MultiKeyGameResource;
import emu.lunarcore.data.resource.ResourceType;
import emu.lunarcore.data.resource.ResourceType.LoadPriority;
import lombok.Getter;

@Getter
@ResourceType(name = {"RelicExpType.json"}, loadPriority = LoadPriority.NORMAL)
public class RelicExpTypeExcel extends MultiKeyGameResource {
    private int TypeID;
    private int Level;
    private int Exp;

    @Override
    public int getPrimaryKey() {
        return TypeID;
    }

    @Override
    public int getSecondaryKey() {
        return Level;
    }

    @Override
    public void onLoad() {

    }
}
