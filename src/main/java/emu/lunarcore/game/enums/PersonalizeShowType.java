package emu.lunarcore.game.enums;

import lombok.Getter;

@Getter
public enum PersonalizeShowType {
    None            (0),
    Always          (1),
    AfterStart      (2),
    InSchedule      (3),
    UnlockedOnly    (4);
    
    private int val;

    private PersonalizeShowType(int value) {
        this.val = value;
    }
}
