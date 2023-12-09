package emu.lunarcore.data.excel;

import org.h2.util.Utils;

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
    
    private int[] FinishMainMissionList = Utils.EMPTY_INT_ARRAY;
    private int[] FinishSubMissionList = Utils.EMPTY_INT_ARRAY;

    @Override
    public int getId() {
        return ID;
    }
}
