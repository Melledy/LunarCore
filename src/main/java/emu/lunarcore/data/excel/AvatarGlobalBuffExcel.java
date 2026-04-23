package emu.lunarcore.data.excel;

import emu.lunarcore.data.resource.GameResource;
import emu.lunarcore.data.resource.ResourceType;

import lombok.Getter;

@Getter
@ResourceType(name = "AvatarGlobalBuffConfig.json")
public class AvatarGlobalBuffExcel extends GameResource {
    private int AvatarID;
    private int MazeBuffID;

    @Override
    public int getId() {
        return AvatarID;
    }

    @Override
    public void onLoad() {

    }
}
