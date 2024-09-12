package emu.lunarcore.game.enums;

import lombok.Getter;

public enum StageType {
    Unknown (0),
    Mainline (1),
    Maze (2),
    Adventure (3),
    Cocoon (4),
    FarmElement (5),
    Client (6),
    FarmRelic (7),
    VerseSimulation (8),
    Trial (9),
    PunkLord (10),
    FightActivity (11),
    TrialAdventure (12),
    BoxingClub (13),
    TrialInBattle (14),
    RogueChallengeActivity (15),
    TreasureDungeon (16),
    AetherDivide (17),
    FantasticStory (18),
    BattleCollege (19),
    Heliobus (20),
    RogueEndlessActivity (21),
    StrongChallengeActivity (22),
    Challenge (23),
    TelevisionActivity (24),
    EvolveBuildActivity (25),
    FeverTimeActivity (26),
    ClockParkActivity (27),
    RogueRelic (28),
    SwordTraining (29),
    StarFightActivity (30),
    SummonActivity (31),
    FightFest (32);
    
    @Getter
    private final int val;
    
    private StageType(int value) {
        this.val = value;
    }
}
