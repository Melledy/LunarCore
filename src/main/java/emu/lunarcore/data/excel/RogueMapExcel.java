package emu.lunarcore.data.excel;

import emu.lunarcore.data.GameDepot;
import emu.lunarcore.data.GameResource;
import emu.lunarcore.data.ResourceType;
import emu.lunarcore.data.ResourceType.LoadPriority;
import lombok.Getter;

@Getter
@ResourceType(name = {"RogueMap.json"}, loadPriority = LoadPriority.HIGH)
public class RogueMapExcel extends GameResource {
    private int RogueMapID;
    private int SiteID;
    private boolean IsStart;
    private int[] NextSiteIDList;
    private int[] LevelList;

    @Override
    public int getId() {
        return (RogueMapID << 8) + SiteID;
    }

    @Override
    public void onLoad() {
        GameDepot.getRogueMapsById(this.getRogueMapID()).add(this);
    }

}
