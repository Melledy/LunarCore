package emu.lunarcore.game.enums;

import lombok.Getter;

@Getter
public enum PropType {
    PROP_NONE (0),
    PROP_ORDINARY (1),
    PROP_SUMMON (2),
    PROP_DESTRUCT (3),
    PROP_SPRING (4),
    PROP_PLATFORM (5),
    PROP_TREASURE_CHEST (6),
    PROP_MATERIAL_ZONE (7),
    PROP_COCOON (8),
    PROP_MAPPINGINFO (9),
    PROP_PUZZLES (10),
    PROP_ELEVATOR (11),
    PROP_NO_REWARD_DESTRUCT (12),
    PROP_LIGHT (13),
    PROP_ROGUE_DOOR (14),
    PROP_ROGUE_OBJECT (15),
    PROP_ROGUE_CHEST (16),
    PROP_TELEVISION (17),
    PROP_RELIC (18),
    PROP_ELEMENT (19),
    PROP_ROGUE_HIDDEN_DOOR (20),
    PROP_PERSPECTIVE_WALL (21),
    PROP_MAZE_PUZZLE (22),
    PROP_MAZE_DECAL (23),
    PROP_ROGUE_REWARD_OBJECT (24),
    PROP_MAP_ROTATION_CHARGER (25),
    PROP_MAP_ROTATION_VOLUME (26),
    PROP_MAP_ROTATION_SWITCHER (27),
    PROP_BOXMAN_BINDED (28);

    private final int val;

    private PropType(int value) {
        this.val = value;
    }
}
