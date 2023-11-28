package emu.lunarcore.data.excel;

import emu.lunarcore.data.GameData;
import emu.lunarcore.data.GameResource;
import emu.lunarcore.data.ResourceType;
import emu.lunarcore.data.ResourceType.LoadPriority;
import emu.lunarcore.game.inventory.GameItem;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import lombok.Getter;

@Getter
@ResourceType(name = {"PhoneThemeConfig.json"}, loadPriority = LoadPriority.LOW)
public class PhoneThemeExcel extends GameResource {
    private int ID;
    private String ShowType;

    @Override
    public int getId() {
        return ID;
    }
}
