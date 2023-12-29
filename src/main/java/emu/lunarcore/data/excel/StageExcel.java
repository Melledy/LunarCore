package emu.lunarcore.data.excel;

import java.util.ArrayList;
import java.util.List;

import emu.lunarcore.data.GameResource;
import emu.lunarcore.data.ResourceType;
import emu.lunarcore.game.enums.StageType;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import lombok.AccessLevel;
import lombok.Getter;

@Getter
@ResourceType(name = {"StageConfig.json"})
public class StageExcel extends GameResource {
    private int StageID;
    private long StageName;
    private StageType StageType;
    private int Level;

    @Getter(AccessLevel.NONE)
    private List<StageMonsterWave> MonsterList;

    // Cache
    private transient List<IntList> monsterWaves;

    @Override
    public int getId() {
        return StageID;
    }

    @Override
    public void onLoad() {
        // Safety check for future versions
        if (this.StageType == null) {
            this.StageType = emu.lunarcore.game.enums.StageType.Unknown;
        }
        
        // Cache monster list
        this.monsterWaves = new ArrayList<>();
        
        for (StageMonsterWave wave : MonsterList) {
            var monsterIds = wave.toList();
            
            if (!monsterIds.isEmpty()) {
                this.monsterWaves.add(monsterIds);
            }
        }
    }

    public static class StageMonsterWave {
        private int Monster0;
        private int Monster1;
        private int Monster2;
        private int Monster3;
        private int Monster4;

        // Sigh...
        public IntList toList() {
            IntList list = new IntArrayList(5);

            if (this.Monster0 != 0) {
                list.add(this.Monster0);
            } if (this.Monster1 != 0) {
                list.add(this.Monster1);
            } if (this.Monster2 != 0) {
                list.add(this.Monster2);
            } if (this.Monster3 != 0) {
                list.add(this.Monster3);
            } if (this.Monster4 != 0) {
                list.add(this.Monster4);
            }

            return list;
        }
    }
}
