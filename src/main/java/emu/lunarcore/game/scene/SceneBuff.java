package emu.lunarcore.game.scene;

import emu.lunarcore.proto.BuffInfoOuterClass.BuffInfo;
import lombok.Getter;

@Getter
public class SceneBuff {
    private int casterAvatarId; // Owner avatar id
    private int buffId;
    private int buffLevel;
    private float duration;
    private long createTime;
    private long expiry;
    
    public SceneBuff(int buffId) {
        this.buffId = buffId;
        this.buffLevel = 1;
        this.createTime = System.currentTimeMillis();
        this.duration = -1;
    }
    
    public SceneBuff(int casterAvatarId, int buffId) {
        this(buffId);
        this.casterAvatarId = casterAvatarId;
        this.expiry = Long.MAX_VALUE;
    }
    
    public SceneBuff(int casterAvatarId, int buffId, int seconds) {
        this(buffId);
        this.casterAvatarId = casterAvatarId;
        this.duration = seconds * 1000;
        this.expiry = this.createTime + (long) duration;
    }
    
    public boolean isExpired(long timestamp) {
        return timestamp > this.expiry;
    }
    
    // Serialization
    
    public BuffInfo toProto() {
        var proto = BuffInfo.newInstance()
                .setBuffId(this.getBuffId())
                .setLevel(this.getBuffLevel())
                .setBaseAvatarId(this.getCasterAvatarId())
                .setAddTimeMs(this.getCreateTime())
                .setLifeTime(this.getDuration())
                .setCount(1);
        
        return proto;
    }
}
