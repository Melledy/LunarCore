package emu.lunarcore.data.excel;

import emu.lunarcore.data.GameResource;
import emu.lunarcore.data.ResourceType;
import lombok.Getter;

@Getter
@ResourceType(name = {"StageConfig.json"})
public class StageExcel extends GameResource {
    private int StageID;
    private long StageName;
    private int Level;

    @Override
    public int getId() {
        return StageID;
    }

    @Override
    public void onLoad() {

    }
}
