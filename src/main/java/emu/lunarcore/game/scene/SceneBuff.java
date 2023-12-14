package emu.lunarcore.game.scene;

import emu.lunarcore.proto.BuffInfoOuterClass.BuffInfo;
import lombok.Getter;

@Getter
public class SceneBuff {
    private int casterAvatarId; // Owner avatar id
    private int buffId;
    private int buffLevel;
    private int duration;
    private long createTime;
    private long expiry;
    
    public SceneBuff(int casterAvatarId, int buffId, int seconds) {
        this.casterAvatarId = casterAvatarId;
        this.buffId = buffId;
        this.buffLevel = 1;
        this.createTime = System.currentTimeMillis();
        this.duration = seconds * 1000;
        this.expiry = this.createTime + duration;
    }
    
    // Serialization
    
    public BuffInfo toProto() {
        var proto = BuffInfo.newInstance()
                .setBuffId(this.getBuffId())
                .setLevel(this.getBuffLevel())
                .setBaseAvatarId(this.getCasterAvatarId())
                .setAddTimeMs(this.getCreateTime())
                .setLifeTime(this.getDuration() / 10)
                .setCount(1);
        
        return proto;
    }
}
