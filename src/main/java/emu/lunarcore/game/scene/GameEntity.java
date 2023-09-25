package emu.lunarcore.game.scene;

import emu.lunarcore.proto.SceneEntityInfoOuterClass.SceneEntityInfo;

public interface GameEntity {
    public int getEntityId();

    public void setEntityId(int id);

    public default int getGroupId() {
        return 0;
    }

    public SceneEntityInfo toSceneEntityProto();
}
