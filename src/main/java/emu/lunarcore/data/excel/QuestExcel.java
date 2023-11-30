package emu.lunarcore.data.excel;

import emu.lunarcore.data.GameResource;
import emu.lunarcore.data.ResourceType;
import emu.lunarcore.data.ResourceType.LoadPriority;
import lombok.Getter;

@Getter
@ResourceType(name = {"QuestData.json"}, loadPriority = LoadPriority.NORMAL)
public class QuestExcel extends GameResource {
    private int QuestID;

    @Override
    public int getId() {
        return QuestID;
    }
}
