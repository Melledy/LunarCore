package emu.lunarcore.data.excel;

import emu.lunarcore.data.GameResource;
import emu.lunarcore.data.ResourceType;
import lombok.Getter;

@Getter
@ResourceType(name = {"RogueMonster.json"})
public class RogueMonsterExcel extends GameResource {
    private int RogueMonsterID;
    private int NpcMonsterID;
    private int EventID;

    @Override
    public int getId() {
        return RogueMonsterID;
    }

}
