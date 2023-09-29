package emu.lunarcore.data.excel;

import emu.lunarcore.data.GameDepot;
import emu.lunarcore.data.GameResource;
import emu.lunarcore.data.ResourceType;
import emu.lunarcore.data.ResourceType.LoadPriority;
import emu.lunarcore.game.enums.AvatarPropertyType;
import lombok.Getter;

@Getter
@ResourceType(name = {"RelicMainAffixConfig.json"}, loadPriority = LoadPriority.NORMAL)
public class RelicMainAffixExcel extends GameResource {
    private int GroupID;
    private int AffixID;
    private AvatarPropertyType Property;

    private double BaseValue;
    private double LevelAdd;

    private boolean IsAvailable;

    @Override
    public int getId() {
        return (GroupID << 16) + AffixID;
    }

    @Override
    public void onLoad() {
        GameDepot.addRelicMainAffix(this);
    }
}
