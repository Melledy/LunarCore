package emu.lunarcore.data.excel;

import java.util.List;

import emu.lunarcore.data.GameDepot;
import emu.lunarcore.data.GameResource;
import emu.lunarcore.data.ResourceType;
import emu.lunarcore.data.ResourceType.LoadPriority;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import lombok.Getter;

@Getter
@ResourceType(name = {"RogueAreaConfig.json"}, loadPriority = LoadPriority.LOW)
public class RogueAreaExcel extends GameResource {
    private int RogueAreaID;
    private int AreaProgress;
    private int Difficulty;
    private Int2IntOpenHashMap ScoreMap;

    private transient int mapId;
    private transient List<RogueMapExcel> sites;

    @Override
    public int getId() {
        return RogueAreaID;
    }

    public boolean isValid() {
        return this.sites != null && this.sites.size() > 0;
    }

    @Override
    public void onLoad() {
        this.mapId = (this.AreaProgress * 100) + this.Difficulty;
        this.sites = GameDepot.getRogueMapsById(this.getMapId());
    }
}
