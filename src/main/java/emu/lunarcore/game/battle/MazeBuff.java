package emu.lunarcore.game.battle;

import emu.lunarcore.data.excel.MazeBuffExcel;
import emu.lunarcore.game.scene.entity.GameEntity;
import emu.lunarcore.proto.BattleBuffOuterClass.BattleBuff;
import emu.lunarcore.proto.BattleBuffOuterClass.BattleBuff.DynamicValuesEntry;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.objects.Object2DoubleMap;
import it.unimi.dsi.fastutil.objects.Object2DoubleOpenHashMap;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Getter
public class MazeBuff {
    private int id;
    private int level;
    private int ownerIndex;
    private int waveFlag;
    private IntList targetIndex;
    
    @Getter(AccessLevel.NONE)
    private Object2DoubleMap<String> dynamicValues;
    
    @Setter
    private transient GameEntity owner;
    
    public MazeBuff(MazeBuffExcel excel, int ownerIndex, int waveFlag) {
        this(excel.getBuffId(), excel.getLv(), ownerIndex, waveFlag);
    }
    
    public MazeBuff(int id, int level, int ownerIndex, int waveFlag) {
        this.id = id;
        this.level = level;
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
                .setId(this.getId())
                .setLevel(this.getLevel())
                .setWaveFlag(this.getWaveFlag());
        
        if (this.ownerIndex != 0) {
            proto.setOwnerId(this.getOwnerIndex());
        }
        
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
