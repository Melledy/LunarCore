package emu.lunarcore.data.excel;

import emu.lunarcore.data.GameResource;
import emu.lunarcore.data.ResourceType;
import emu.lunarcore.data.ResourceType.LoadPriority;
import lombok.Getter;

@Getter
@ResourceType(name = {"ChallengeTargetConfig.json", "ChallengeStoryTargetConfig.json"}, loadPriority = LoadPriority.HIGH)
public class ChallengeTargetExcel extends GameResource {
    private int ID;
    private ChallengeType ChallengeTargetType;
    private int ChallengeTargetParam1;

    @Override
    public int getId() {
        return ID;
    }

    public enum ChallengeType {
        None, ROUNDS, DEAD_AVATAR, KILL_MONSTER, AVATAR_BASE_TYPE_MORE, AVATAR_BASE_TYPE_LESS, ROUNDS_LEFT, TOTAL_SCORE;
    }
}
