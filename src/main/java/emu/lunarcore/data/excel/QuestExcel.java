package emu.lunarcore.data.excel;

import emu.lunarcore.data.resource.GameResource;
import emu.lunarcore.data.resource.ResourceType;
import emu.lunarcore.data.resource.ResourceType.LoadPriority;
import lombok.Getter;

@Getter
@ResourceType(name = {"QuestData.json"}, loadPriority = LoadPriority.NORMAL)
public class QuestExcel extends GameResource {
    private int QuestID;
    private int QuestType = 0;
    private int RewardID = 0;

    @Override
    public int getId() {
        return QuestID;
    }
}
