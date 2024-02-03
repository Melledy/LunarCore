package emu.lunarcore.data.excel;

import emu.lunarcore.data.GameData;
import emu.lunarcore.data.GameResource;
import emu.lunarcore.data.ResourceType;
import lombok.Getter;

@Getter
@ResourceType(name = {"RogueNousSubStory.json"})
public class RogueNousSubStoryExcel extends GameResource {
    private int StoryID;
    private int Layer;
    
    @Override
    public int getId() {
        return this.StoryID;
    }

    @Override
    public void onLoad() {
        GameData.getRogueNousSubStoryExcelMap().put(this.StoryID, this);
    }
}
