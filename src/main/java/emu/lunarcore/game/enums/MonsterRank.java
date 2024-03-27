package emu.lunarcore.game.enums;

import lombok.Getter;

@Getter
public enum MonsterRank {
    Unknow (0),
    Minion (1),
    MinionLv2 (2),
    Elite (3),
    LittleBoss (4),
    BigBoss (5);

    private final int val;

    private MonsterRank(int value) {
        this.val = value;
    }
}
