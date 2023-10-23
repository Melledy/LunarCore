package emu.lunarcore.game.rogue;

import lombok.Getter;

public enum RogueRoomType {
    UNKNOWN (0),
    COMBAT_1 (1),
    COMBAT_2 (2),
    OCCURRENCE (3),
    ENCOUNTER (4),
    RESPITE (5),
    ELITE (6),
    BOSS (7),
    TRANSACTION (8),
    ADVENTURE (9);
    
    @Getter
    private final int val;
    
    private RogueRoomType(int value) {
        this.val = value;
    }
}
