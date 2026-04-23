package emu.lunarcore.data.excel;

import emu.lunarcore.data.resource.GameResource;
import emu.lunarcore.data.resource.ResourceType;
import emu.lunarcore.game.enums.MonsterRank;
import lombok.Getter;

@Getter
@ResourceType(name = {"NPCMonsterData.json"})
public class NpcMonsterExcel extends GameResource {
    private int ID;
    private long NPCName;
    private MonsterRank Rank;

    @Override
    public int getId() {
        return ID;
    }

    @Override
    public void onLoad() {

    }
}
