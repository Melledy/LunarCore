package emu.lunarcore.data.excel;

import java.util.ArrayList;

import emu.lunarcore.data.GameDepot;
import emu.lunarcore.data.GameResource;
import emu.lunarcore.data.ResourceType;
import emu.lunarcore.data.ResourceType.LoadPriority;
import lombok.Getter;

@Getter
@ResourceType(name = {"ChallengeMazeRewardLine.json", "ChallengeStoryRewardLine.json"}, loadPriority = LoadPriority.LOW)
public class ChallengeRewardExcel extends GameResource {
    private int GroupID;
    private int StarCount;
    private int RewardID;

    @Override
    public int getId() {
        return (GroupID << 16) + StarCount;
    }
    
    @Override
    public void onLoad() {
        var rewardLine = GameDepot.getChallengeRewardLines().computeIfAbsent(GroupID, id -> new ArrayList<>());
        rewardLine.add(this);
    }
}
