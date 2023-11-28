package emu.lunarcore.data.excel;

import java.util.List;

import emu.lunarcore.data.GameResource;
import emu.lunarcore.data.ResourceType;
import emu.lunarcore.data.common.ItemParam;
import lombok.Getter;

@Getter
@ResourceType(name = {"MonsterConfig.json"})
public class MonsterExcel extends GameResource {
    private int MonsterID;
    
    @Override
    public int getId() {
        return MonsterID;
    }
}
