package emu.lunarcore.data.excel;

import emu.lunarcore.data.resource.GameResource;
import emu.lunarcore.data.resource.ResourceType;
import lombok.Getter;

@Getter
@ResourceType(name = {"PlayerOutfitBase.json"})
public class PlayerOutfitBaseExcel extends GameResource {
    private int OutfitID;
    private String[] SlotTypeList;
    private int ItemID;

    @Override
    public int getId() {
        return OutfitID;
    }
}
