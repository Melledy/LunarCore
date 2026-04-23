package emu.lunarcore.data.excel;

import emu.lunarcore.data.resource.MultiKeyGameResource;
import emu.lunarcore.data.resource.ResourceType;
import lombok.Getter;

@Getter
@ResourceType(name = {"AvatarConfigEnhanced.json"})
public class AvatarEnhanceExcel extends MultiKeyGameResource {
    private int AvatarID;
    private int EnhancedID;

    @Override
    public int getPrimaryKey() {
        return AvatarID;
    }

    @Override
    public int getSecondaryKey() {
        return EnhancedID;
    }

    @Override
    public void onLoad() {
        
    }
}
