package emu.lunarcore.data.excel;

import emu.lunarcore.data.GameResource;
import emu.lunarcore.data.ResourceType;
import lombok.Getter;

@Getter
@ResourceType(name = {"MonsterConfig.json"})
public class MonsterExcel extends GameResource {
    private int MonsterID;
    private int MonsterTemplateID;
    private long MonsterName;

    @Override
    public int getId() {
        return MonsterID;
    }
}
