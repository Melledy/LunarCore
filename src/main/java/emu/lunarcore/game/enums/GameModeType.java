package emu.lunarcore.game.enums;

import lombok.Getter;

@Getter
public enum GameModeType {
    Unknown (0),
    Town (1),
    Maze (2),
    Train (3),
    Challenge (4),
    RogueExplore (5),
    RogueChallenge (6),
    TownRoom (7),
    Raid (8),
    FarmRelic (9),
    Client (10),
    ChallengeActivity (11),
    ActivityPunkLord (12),
    RogueAeonRoom (13),
    TrialActivity (14),
    AetherDivide (15),
    ChessRogue (16);

    private final int val;

    private GameModeType(int value) {
        this.val = value;
    }
}