package emu.lunarcore.game.rogue;

import emu.lunarcore.game.battle.MazeBuff;
import emu.lunarcore.proto.RogueBuffOuterClass.RogueBuff;
import lombok.Getter;

@Getter
public class RogueBuffData {
    private int buffId;
    private int level;
    
    public RogueBuffData(int buffId, int level) {
        this.buffId = buffId;
        this.level = level;
    }
    
    public MazeBuff toMazeBuff() {
        return new MazeBuff(buffId, level, 0, 0xffffffff);
    }
    
    public RogueBuff toProto() {
        var proto = RogueBuff.newInstance()
                .setBuffId(this.getBuffId())
                .setLevel(this.getLevel());
        
        return proto;
    }
}
