package emu.lunarcore.game.battle;

import emu.lunarcore.data.excel.MazeBuffExcel;
import emu.lunarcore.proto.BattleBuffOuterClass.BattleBuff;
import emu.lunarcore.proto.BattleBuffOuterClass.BattleBuff.DynamicValuesEntry;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.objects.Object2DoubleMap;
import it.unimi.dsi.fastutil.objects.Object2DoubleOpenHashMap;
import lombok.AccessLevel;
import lombok.Getter;

@Getter
public class MazeBuff {
    private MazeBuffExcel excel;
    private int ownerIndex;
    private int waveFlag;
    private IntList targetIndex;
    
    @Getter(AccessLevel.NONE)
    private Object2DoubleMap<String> dynamicValues; 
    
    public MazeBuff(MazeBuffExcel excel, int ownerIndex, int waveFlag) {
        this.excel = excel;
        this.ownerIndex = ownerIndex;
        this.waveFlag = waveFlag;
    }
    
    public void addTargetIndex(int index) {
        if (this.targetIndex == null) {
            this.targetIndex = new IntArrayList();
        }
        
        this.targetIndex.add(index);
    }
    
    public void addDynamicValue(String key, double value) {
        if (this.dynamicValues == null) {
            this.dynamicValues = new Object2DoubleOpenHashMap<>();
        }
        
        this.dynamicValues.put(key, value);
    }
    
    public BattleBuff toProto() {
        var proto = BattleBuff.newInstance()
                .setId(excel.getBuffId())
                .setLevel(excel.getLv())
                .setOwnerId(this.getOwnerIndex())
                .setWaveFlag(this.getWaveFlag());
        
        if (this.targetIndex != null) {
            for (int index : this.targetIndex) {
                proto.addTargetIndexList(index);
            }
        }
        
        if (this.dynamicValues != null) {
            for (var entry : this.dynamicValues.object2DoubleEntrySet()) {
                var dynamicValue = DynamicValuesEntry.newInstance()
                        .setKey(entry.getKey())
                        .setValue((float) entry.getDoubleValue());
                
                proto.addDynamicValues(dynamicValue);
            }
        }
        
        return proto;
    }
}
