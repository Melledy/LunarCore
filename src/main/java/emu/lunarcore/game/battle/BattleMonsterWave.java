package emu.lunarcore.game.battle;

import emu.lunarcore.data.excel.StageExcel;
import emu.lunarcore.proto.SceneMonsterOuterClass.SceneMonster;
import emu.lunarcore.proto.SceneMonsterWaveOuterClass.SceneMonsterWave;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import lombok.Getter;
import lombok.Setter;

@Getter
public class BattleMonsterWave {
    private final StageExcel stage;
    private IntList monsters;
    
    @Setter
    private int customLevel;
    
    public BattleMonsterWave(StageExcel stage) {
        this.stage = stage;
        this.monsters = new IntArrayList();
    }
    
    public SceneMonsterWave toProto() {
        var proto = SceneMonsterWave.newInstance()
                .setWaveId(1) // Probably not named correctly
                .setStageId(stage.getId());
        
        // Set wave params
        proto.getMutableWaveParam();
        
        if (this.customLevel > 0) {
            proto.getMutableWaveParam().setLevel(this.customLevel);
        }
        
        for (int monsterId : monsters) {
            var monster = SceneMonster.newInstance().setMonsterId(monsterId);
            proto.addMonsterList(monster);
        }
        
        return proto;
    }
}
