package emu.lunarcore.data.excel;

import emu.lunarcore.data.GameResource;
import emu.lunarcore.data.ResourceType;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import lombok.Getter;

@Getter
@ResourceType(name = {"ChallengeMazeConfig.json", "ChallengeStoryMazeConfig.json"})
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

    private transient Int2ObjectMap<ChallengeMonsterInfo> challengeMonsters1;
    private transient Int2ObjectMap<ChallengeMonsterInfo> challengeMonsters2;
    
    private transient ChallengeStoryExtraExcel storyExcel;

    @Override
    public int getId() {
        return ID;
    }
    
    public boolean isStory() {
        return this.storyExcel != null;
    }

    public void setStoryExcel(ChallengeStoryExtraExcel storyExcel) {
        this.storyExcel = storyExcel;
        this.ChallengeCountDown = storyExcel.getTurnLimit();
    }

    @Override
    public void onLoad() {
        // Cache challenge monsters
        this.challengeMonsters1 = new Int2ObjectOpenHashMap<>();
        for (int i = 0; i < ConfigList1.length; i++) {
            if (ConfigList1[i] == 0) break;

            var monster = new ChallengeMonsterInfo(ConfigList1[i], NpcMonsterIDList1[i], EventIDList1[i]);
            this.challengeMonsters1.put(monster.getConfigId(), monster);
        }

        this.challengeMonsters2 = new Int2ObjectOpenHashMap<>();
        for (int i = 0; i < ConfigList2.length; i++) {
            if (ConfigList2[i] == 0) break;

            var monster = new ChallengeMonsterInfo(ConfigList2[i], NpcMonsterIDList2[i], EventIDList2[i]);
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

    @Getter
    public static class ChallengeMonsterInfo {
        private int configId;
        private int npcMonsterId;
        private int eventId;

        public ChallengeMonsterInfo(int configId, int npcMonsterId, int eventId) {
            this.configId = configId;
            this.npcMonsterId = npcMonsterId;
            this.eventId = eventId;
        }

    }
}
