package emu.lunarcore.data.excel;

import emu.lunarcore.data.GameResource;
import emu.lunarcore.data.ResourceType;
import lombok.Getter;

@Getter
@ResourceType(name = {"BackGroundMusic.json"})
public class BackGroundMusicExcel extends GameResource {
    private int ID;
    private int GroupID;

    @Override
    public int getId() {
        return ID;
    }
    public int getGroupId() {
        return GroupID;
    }

}
