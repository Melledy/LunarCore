package emu.lunarcore.data.excel;

import emu.lunarcore.data.GameResource;
import emu.lunarcore.data.ResourceType;
import lombok.Getter;

@Getter
@ResourceType(name = {"ChallengeMazeConfig.json"})
public class ChallengeExcel extends GameResource {
    private int ID;
    private int MapEntranceID;
    private int StageNum;
    private int ChallengeCountDown;
    private int MazeBuffID;
    
    private int[] ChallengeTargetID;
    
    private int MazeGroupID1;
    private int[] ConfigList1;
    private int[] NpcMonsterIDList1;
    private int[] EventIDList1;
    private int MazeGroupID2;
    private int[] ConfigList2;
    private int[] NpcMonsterIDList2;
    private int[] EventIDList2;
    
    @Override
    public int getId() {
        return ID;
    }

}
