package emu.lunarcore.game.battle;

import emu.lunarcore.data.excel.MazeBuffExcel;
import emu.lunarcore.proto.BattleBuffOuterClass.BattleBuff;
import emu.lunarcore.proto.BattleBuffOuterClass.BattleBuff.DynamicValuesEntry;
import it.unimi.dsi.fastutil.objects.Object2DoubleMap;
import it.unimi.dsi.fastutil.objects.Object2DoubleOpenHashMap;
import lombok.AccessLevel;
import lombok.Getter;

@Getter
public class MazeBuff {
    private MazeBuffExcel excel;
    private int ownerEntityId;
    private int waveFlag;
    
    @Getter(AccessLevel.NONE)
    private Object2DoubleMap<String> dynamicValues; 
    
    public MazeBuff(MazeBuffExcel excel, int ownerEntityId, int waveFlag) {
        this.excel = excel;
        this.ownerEntityId = ownerEntityId;
        this.waveFlag = waveFlag;
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
                .setOwnerId(this.getOwnerEntityId())
                .setWaveFlag(this.getWaveFlag());
        
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
