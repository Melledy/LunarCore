package emu.lunarcore.game.rogue;

import lombok.Getter;

@Getter
public enum RogueBuffType {
    Default         (100),
    Preservation    (120),
    Remembrance     (121),
    Nihility        (122),
    Abundance       (123),
    Hunt            (124),
    Destruction     (125),
    Elation         (126),
    Propagation     (127);
    
    private final int val;
    
    private RogueBuffType(int val) {
        this.val = val;
    }
}
