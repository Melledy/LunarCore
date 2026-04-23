package emu.lunarcore.data.excel;

import emu.lunarcore.data.resource.GameResource;
import emu.lunarcore.data.resource.ResourceType;
import lombok.Getter;

@Getter
@ResourceType(name = {"ContentPackageConfig.json"})
public class ContentPackageExcel extends GameResource {
    private int ContentID;

    @Override
    public int getId() {
        return ContentID;
    }
}
