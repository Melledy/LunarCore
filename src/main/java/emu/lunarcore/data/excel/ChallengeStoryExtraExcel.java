package emu.lunarcore.data.excel;

import emu.lunarcore.data.GameData;
import emu.lunarcore.data.GameResource;
import emu.lunarcore.data.ResourceType;
import emu.lunarcore.data.ResourceType.LoadPriority;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import lombok.Getter;

@Getter
@ResourceType(name = {"ChallengeStoryMazeExtra.json"}, loadPriority = LoadPriority.LOW)
public class ChallengeStoryExtraExcel extends GameResource {
    private int ID;
    private int TurnLimit;
    private int ClearScore;
    private IntArrayList BattleTargetID;

    @Override
    public int getId() {
        return ID;
    }

    @Override
    public void onLoad() {
        var challengeExcel = GameData.getChallengeExcelMap().get(this.getId());
        if (challengeExcel != null) {
            challengeExcel.setStoryExcel(this);
        }
    }
}
