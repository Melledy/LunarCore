package emu.lunarcore.data.excel;

import emu.lunarcore.data.resource.GameResource;
import emu.lunarcore.data.resource.ResourceType;
import lombok.Getter;

@Getter
@ResourceType(name = {"TutorialGuideGroup.json"})
public class TutorialGuideExcel extends GameResource {
    private int GroupID;

    @Override
    public int getId() {
        return GroupID;
    }
}
