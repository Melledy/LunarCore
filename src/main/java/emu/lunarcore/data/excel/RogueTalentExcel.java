package emu.lunarcore.data.excel;

import java.util.List;

import emu.lunarcore.data.GameResource;
import emu.lunarcore.data.ResourceType;
import emu.lunarcore.data.common.ItemParam;
import lombok.Getter;

@Getter
@ResourceType(name = {"RogueTalent.json"})
public class RogueTalentExcel extends GameResource {
    private int TalentID;
    private List<ItemParam> Cost;

    @Override
    public int getId() {
        return TalentID;
    }

}
