package emu.lunarcore.game.scene.triggers;

import emu.lunarcore.game.scene.Scene;

public abstract class PropTrigger {
    
    public abstract PropTriggerType getType();
    
    public abstract boolean shouldRun(int param);
    
    public abstract void run(Scene scene);

}