package emu.lunarcore.data.excel;

import emu.lunarcore.data.common.ExcelMonsterParam;
import emu.lunarcore.data.resource.GameResource;
import emu.lunarcore.data.resource.ResourceType;
import emu.lunarcore.game.challenge.ChallengeType;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import lombok.Getter;

@Getter
@ResourceType(name = {"ChallengeMazeConfig.json", "ChallengeStoryMazeConfig.json", "ChallengeBossMazeConfig.json"})
public class ChallengeExcel extends GameResource {
    private int ID;
    private int GroupID;
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

    private transient Int2ObjectMap<ExcelMonsterParam> challengeMonsters1;
    private transient Int2ObjectMap<ExcelMonsterParam> challengeMonsters2;

    private transient ChallengeType type = ChallengeType.MEMORY;
    private transient ChallengeStoryExtraExcel storyExcel;
    private transient ChallengeBossExtraExcel bossExcel;

    @Override
    public int getId() {
        return ID;
    }

    public boolean isStory() {
        return this.storyExcel != null;
    }

    public void setStoryExcel(ChallengeStoryExtraExcel excel) {
        this.type = ChallengeType.STORY;
        this.storyExcel = excel;
        this.ChallengeCountDown = storyExcel.getTurnLimit();
    }

    public void setBossExcel(ChallengeBossExtraExcel excel) {
        this.type = ChallengeType.BOSS;
        this.bossExcel = excel;
        this.ChallengeCountDown = 10; // idk
    }

    @Override
    public void onLoad() {
        // Cache challenge monsters
        this.challengeMonsters1 = new Int2ObjectOpenHashMap<>();
        for (int i = 0; i < ConfigList1.length; i++) {
            if (ConfigList1[i] == 0) break;

            var monster = new ExcelMonsterParam(ConfigList1[i], NpcMonsterIDList1[i], EventIDList1[i]);
            this.challengeMonsters1.put(monster.getConfigId(), monster);
        }

        this.challengeMonsters2 = new Int2ObjectOpenHashMap<>();
        for (int i = 0; i < ConfigList2.length; i++) {
            if (ConfigList2[i] == 0) break;

            var monster = new ExcelMonsterParam(ConfigList2[i], NpcMonsterIDList2[i], EventIDList2[i]);
            this.challengeMonsters2.put(monster.getConfigId(), monster);
        }

        // Clear arrays to save memory
        this.ConfigList1 = null;
        this.NpcMonsterIDList1 = null;
        this.EventIDList1 = null;
        this.ConfigList2 = null;
        this.NpcMonsterIDList2 = null;
        this.EventIDList2 = null;
    }
}
