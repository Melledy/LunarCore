package emu.lunarcore.game.enums;

import emu.lunarcore.game.challenge.ChallengeEntityLoader;
import emu.lunarcore.game.rogue.RogueEntityLoader;
import emu.lunarcore.game.scene.SceneEntityLoader;
import lombok.Getter;

@Getter
public enum PlaneType {
    Unknown         (0),
    Town            (1),
    Maze            (2),
    Train           (3),
    Challenge       (4, new ChallengeEntityLoader()),
    Rogue           (5, new RogueEntityLoader()),
    Raid            (6),
    AetherDivide    (7),
    TrialActivity   (8);

    private final int val;
    private final SceneEntityLoader sceneEntityLoader;

    private PlaneType(int value) {
        this(value, new SceneEntityLoader());
    }
    
    private PlaneType(int value, SceneEntityLoader entityLoader) {
        this.val = value;
        this.sceneEntityLoader = entityLoader;
    }
}