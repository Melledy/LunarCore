package emu.lunarcore.game.rogue;

import emu.lunarcore.data.GameData;
import emu.lunarcore.data.excel.RogueBuffExcel;
import emu.lunarcore.game.battle.MazeBuff;
import emu.lunarcore.proto.RogueBuffDataOuterClass;
import emu.lunarcore.proto.RogueBuffOuterClass.RogueBuff;
import emu.lunarcore.proto.RogueCommonBuffOuterClass.RogueCommonBuff;
import lombok.Getter;

@Getter
public class RogueBuffData {
    private int id;
    private int level;
    
    private transient RogueBuffExcel excel;
    
    public RogueBuffData(int buffId, int level) {
        this.id = buffId;
        this.level = level;
    }
    
    public RogueBuffExcel getExcel() {
        if (this.excel == null) {
            this.excel = GameData.getRogueBuffExcel(this.getId(), this.getLevel());
        }
        
        return this.excel;
    }
    
    public MazeBuff toMazeBuff() {
        return new MazeBuff(id, level, 0xffffffff, 0xffffffff);
    }
    
    public RogueBuff toProto() {
        var proto = RogueBuff.newInstance()
                .setBuffId(this.getId())
                .setLevel(this.getLevel());
        
        return proto;
    }
    
    public RogueCommonBuff toCommonProto() {
        var proto = RogueCommonBuff.newInstance()
                .setBuffId(this.getId())
                .setLevel(this.getLevel());
        
        return proto;
    }
    
    public RogueBuffDataOuterClass.RogueBuffData toDataProto() {
        var proto = RogueBuffDataOuterClass.RogueBuffData.newInstance()
                .setBuffId(this.getId())
                .setLevel(this.getLevel());
        
        return proto;
    }
}
