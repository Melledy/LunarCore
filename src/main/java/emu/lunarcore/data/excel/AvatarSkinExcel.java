package emu.lunarcore.data.excel;

import emu.lunarcore.data.resource.GameResource;
import emu.lunarcore.data.resource.ResourceType;
import lombok.Getter;

@Getter
@ResourceType(name = {"AvatarSkin.json"})
public class AvatarSkinExcel extends GameResource {
    private int ID;
    private int AvatarID;

    @Override
    public int getId() {
        return ID;
    }
}
