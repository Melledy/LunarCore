package emu.lunarcore.data.excel;

import emu.lunarcore.data.resource.MultiKeyGameResource;
import emu.lunarcore.data.resource.ResourceType;
import lombok.Getter;

@Getter
@ResourceType(name = {"FarmElementConfig.json"})
public class FarmElementExcel extends MultiKeyGameResource {
    private int ID;
    private int WorldLevel;
    private int StaminaCost;
    private int StageID;

    @Override
    public int getPrimaryKey() {
        return ID;
    }

    @Override
    public int getSecondaryKey() {
        return WorldLevel;
    }

    @Override
    public void onLoad() {

    }
}
