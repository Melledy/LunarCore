package emu.lunarcore.data.excel;

import emu.lunarcore.data.GameDepot;
import emu.lunarcore.data.resource.MultiKeyGameResource;
import emu.lunarcore.data.resource.ResourceType;
import emu.lunarcore.data.resource.ResourceType.LoadPriority;
import emu.lunarcore.game.enums.AvatarPropertyType;
import lombok.Getter;

@Getter
@ResourceType(name = {"RelicMainAffixConfig.json"}, loadPriority = LoadPriority.NORMAL)
public class RelicMainAffixExcel extends MultiKeyGameResource {
    private int GroupID;
    private int AffixID;
    private AvatarPropertyType Property;

    private double BaseValue;
    private double LevelAdd;

    private boolean IsAvailable;

    @Override
    public int getPrimaryKey() {
        return GroupID;
    }

    @Override
    public int getSecondaryKey() {
        return AffixID;
    }

    @Override
    public void onLoad() {
        GameDepot.addRelicMainAffix(this);
    }
}
