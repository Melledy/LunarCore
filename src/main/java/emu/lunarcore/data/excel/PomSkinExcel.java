package emu.lunarcore.data.excel;

import emu.lunarcore.data.resource.GameResource;
import emu.lunarcore.data.resource.ResourceType;
import emu.lunarcore.data.resource.ResourceType.LoadPriority;
import lombok.Getter;

@Getter
@ResourceType(name = {"PamSkinConfig.json"}, loadPriority = LoadPriority.NORMAL)
public class PomSkinExcel extends GameResource {
    private int SkinID;

    @Override
    public int getId() {
        return SkinID;
    }
}
