package emu.lunarcore.data.excel;

import java.util.List;

import emu.lunarcore.data.GameResource;
import emu.lunarcore.data.ResourceType;
import emu.lunarcore.data.common.ItemParam;
import lombok.Getter;

@Getter
@ResourceType(name = {"MonsterDrop.json"})
public class MonsterDropExcel extends GameResource {
    private int MonsterTemplateID;
    private int WorldLevel;
    private int AvatarExpReward;

    private List<ItemParam> DisplayItemList;

    @Override
    public int getId() {
        return (MonsterTemplateID << 4) + WorldLevel;
    }

}
