package emu.lunarcore.data.excel;

import emu.lunarcore.data.GameDepot;
import emu.lunarcore.data.GameResource;
import emu.lunarcore.data.ResourceType;
import lombok.Getter;

@Getter
@ResourceType(name = {"RogueBonus.json"})
public class RogueBonusExcel extends GameResource {
    private int BonusID;
    private int BonusEvent;

    @Override
    public int getId() {
        return BonusID;
    }
    
    @Override
    public void onLoad() {
        if (BonusID > 0 && BonusID < 10) {
            GameDepot.getRogueRandomCommonBonusList().add(this);
        }
    }
}
