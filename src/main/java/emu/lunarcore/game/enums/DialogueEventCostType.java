package emu.lunarcore.game.enums;

import lombok.Getter;

@Getter
public enum DialogueEventCostType {
    None(0),
    CostItemValue(1),
    CostHpCurrentPercent(2),
    CostItemPercent(3),
    CostHpSpToPercent(4),
    ;

    private int value;

    DialogueEventCostType(int value) {
        this.value = value;
    }
}
