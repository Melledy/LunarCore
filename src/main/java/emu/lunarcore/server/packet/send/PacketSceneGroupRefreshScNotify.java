package emu.lunarcore.server.packet.send;

import java.util.Collection;

import emu.lunarcore.game.scene.entity.GameEntity;
import emu.lunarcore.proto.SceneEntityRefreshInfoOuterClass.SceneEntityRefreshInfo;
import emu.lunarcore.proto.SceneGroupRefreshInfoOuterClass.SceneGroupRefreshInfo;
import emu.lunarcore.proto.SceneGroupRefreshScNotifyOuterClass.SceneGroupRefreshScNotify;
import emu.lunarcore.proto.SceneGroupRefreshTypeOuterClass.SceneGroupRefreshType;
import emu.lunarcore.server.packet.BasePacket;
import emu.lunarcore.server.packet.CmdId;

public class PacketSceneGroupRefreshScNotify extends BasePacket {

    public PacketSceneGroupRefreshScNotify(GameEntity toAdd, GameEntity toRemove) {
        super(CmdId.SceneGroupRefreshScNotify);

        var group = SceneGroupRefreshInfo.newInstance();

        if (toAdd != null) {
            group.setGroupId(toAdd.getGroupId());
            group.addRefreshEntity(SceneEntityRefreshInfo.newInstance().setAddEntity(toAdd.toSceneEntityProto()));
        } else if (toRemove != null) {
            group.setGroupId(toRemove.getGroupId());
            group.setGroupRefreshType(SceneGroupRefreshType.SCENE_GROUP_REFRESH_TYPE_UNLOAD);
            group.addRefreshEntity(SceneEntityRefreshInfo.newInstance().setDelEntity(toRemove.getEntityId()));
        }

        var data = SceneGroupRefreshScNotify.newInstance()
                .addGroupRefreshInfo(group);

        this.setData(data);
    }

    public PacketSceneGroupRefreshScNotify(Collection<? extends GameEntity> toAdd, Collection<? extends GameEntity> toRemove) {
        super(CmdId.SceneGroupRefreshScNotify);

        var group = SceneGroupRefreshInfo.newInstance();

        if (toAdd != null) {
            for (var entity : toAdd) {
                group.addRefreshEntity(SceneEntityRefreshInfo.newInstance().setAddEntity(entity.toSceneEntityProto()));
            }
        }
        
        if (toRemove != null) {
            for (var entity : toRemove) {
                group.addRefreshEntity(SceneEntityRefreshInfo.newInstance().setDelEntity(entity.getEntityId()));
            }
        }

        var data = SceneGroupRefreshScNotify.newInstance()
                .addGroupRefreshInfo(group);

        this.setData(data);
    }
}
