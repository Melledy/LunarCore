package emu.lunarcore.data.excel;

import emu.lunarcore.data.resource.GameResource;
import emu.lunarcore.data.resource.ResourceType;
import lombok.Getter;

@Getter
@ResourceType(name = {"TutorialData.json"})
public class TutorialExcel extends GameResource {
    private int TutorialID;

    @Override
    public int getId() {
        return TutorialID;
    }
}
