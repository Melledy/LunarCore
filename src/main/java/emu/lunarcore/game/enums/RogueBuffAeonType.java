package emu.lunarcore.game.enums;

import lombok.Getter;

@Getter
public enum RogueBuffAeonType {
    Normal (0),
    BattleEventBuff (1),
    BattleEventBuffEnhance (2),
    BattleEventBuffCross (3);
    
    private final int val;

    private RogueBuffAeonType(int value) {
        this.val = value;
    }
}
