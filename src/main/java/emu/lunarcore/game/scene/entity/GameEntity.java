package emu.lunarcore.game.scene.entity;

import emu.lunarcore.game.scene.Scene;
import emu.lunarcore.proto.SceneEntityInfoOuterClass.SceneEntityInfo;

public interface GameEntity {
    
    public int getEntityId();

    public void setEntityId(int id);

    public default int getGroupId() {
        return 0;
    }
    
    public default int getInstId() {
        return 0;
    }
    
    public default void onAdd(Scene scene) {
        
    }

    public default void onRemove(Scene scene) {
        
    }

    public SceneEntityInfo toSceneEntityProto();
}
