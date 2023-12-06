package emu.lunarcore.data.excel;

import com.google.gson.annotations.SerializedName;

import emu.lunarcore.data.GameResource;
import emu.lunarcore.data.ResourceType;
import emu.lunarcore.game.enums.PlaneType;
import lombok.Getter;

@Getter
@ResourceType(name = {"MazePlane.json"})
public class MazePlaneExcel extends GameResource {
    private int PlaneID;
    private int WorldID;
    private int StartFloorID;
    private long PlaneName;

    @SerializedName(value = "PlaneType")
    private PlaneType planeType = PlaneType.Unknown;

    @Override
    public int getId() {
        return PlaneID;
    }
}
