package emu.lunarcore.data.excel;

import emu.lunarcore.data.GameData;
import emu.lunarcore.data.GameResource;
import emu.lunarcore.data.ResourceType;
import lombok.Getter;

@Getter
@ResourceType(name = {"RogueNousMainStory.json"})
public class RogueNousMainStoryExcel extends GameResource {
    private int StoryID;
    private int Layer;
    
    @Override
    public int getId() {
        return this.StoryID;
    }

    @Override
    public void onLoad() {
        GameData.getRogueNousMainStoryExcelMap().put(this.StoryID, this);
    }
}
