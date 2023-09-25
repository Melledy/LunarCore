package emu.lunarcore.data.excel;

import emu.lunarcore.data.GameResource;
import emu.lunarcore.data.ResourceType;
import lombok.Getter;

@Getter
@ResourceType(name = {"NPCMonsterData.json"})
public class NpcMonsterExcel extends GameResource {
    private int ID;
    private long NPCName;

    @Override
    public int getId() {
        return ID;
    }

    @Override
    public void onLoad() {

    }
}
