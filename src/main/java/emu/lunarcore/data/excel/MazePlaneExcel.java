package emu.lunarcore.data.excel;

import emu.lunarcore.data.GameResource;
import emu.lunarcore.data.ResourceType;
import emu.lunarcore.game.enums.GameModeType;
import lombok.Getter;

@Getter
@ResourceType(name = {"MazePlane.json"})
public class MazePlaneExcel extends GameResource {
    private int PlaneID;
    private int WorldID;
    private GameModeType PlaneType = GameModeType.Unknown;
    
    @Override
    public int getId() {
        return PlaneID;
    }
}
