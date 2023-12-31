package emu.lunarcore.data.config;

import lombok.Getter;

/**
 *  Original name: LevelMonsterInfo
 */
@Getter
public class MonsterInfo extends ObjectInfo {
    private int NPCMonsterID;
    private int EventID;
    private int FarmElementID;
    private boolean IsClientOnly;
}
