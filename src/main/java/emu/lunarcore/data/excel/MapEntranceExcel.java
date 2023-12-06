package emu.lunarcore.data.excel;

import emu.lunarcore.data.GameResource;
import emu.lunarcore.data.ResourceType;
import lombok.Getter;

@Getter
@ResourceType(name = {"MapEntrance.json"})
public class MapEntranceExcel extends GameResource {
    private int ID;
    private int PlaneID;
    private int FloorID;
    private int StartGroupID;
    private int StartAnchorID;

    @Override
    public int getId() {
        return ID;
    }
}
