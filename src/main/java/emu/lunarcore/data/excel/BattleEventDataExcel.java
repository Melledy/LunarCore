package emu.lunarcore.data.excel;

import emu.lunarcore.data.resource.GameResource;
import emu.lunarcore.data.resource.ResourceType;
import lombok.Getter;

@Getter
@ResourceType(name = {"BattleEventData.json"})
public class BattleEventDataExcel extends GameResource {
    private int BattleEventID;
    private String Config;

    @Override
    public int getId() {
        return BattleEventID;
    }

}
