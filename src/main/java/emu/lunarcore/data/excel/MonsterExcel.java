package emu.lunarcore.data.excel;

import emu.lunarcore.data.resource.GameResource;
import emu.lunarcore.data.resource.ResourceType;
import lombok.Getter;

@Getter
@ResourceType(name = {"MonsterConfig.json"})
public class MonsterExcel extends GameResource {
    private int MonsterID;
    private long MonsterName;

    @Override
    public int getId() {
        return MonsterID;
    }
}
