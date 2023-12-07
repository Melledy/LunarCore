package emu.lunarcore.game.scene;

import lombok.Getter;

@Getter
public class SceneEntityBuff {
    private int owner; // Owner avatar id
    private int id;
    private long expiry;
    
    public SceneEntityBuff(int owner, int id, long duration) {
        this.owner = owner;
        this.id = id;
        this.expiry = System.currentTimeMillis() + (duration * 1000);
    }
}
