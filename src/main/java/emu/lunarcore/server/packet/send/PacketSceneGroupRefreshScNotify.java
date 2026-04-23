package emu.lunarcore.server.packet.send;

import java.util.Collection;

import emu.lunarcore.game.scene.Scene;
import emu.lunarcore.game.scene.entity.GameEntity;
import emu.lunarcore.proto.GroupStateInfoOuterClass.GroupStateInfo;
import emu.lunarcore.proto.SceneEntityRefreshInfoOuterClass.SceneEntityRefreshInfo;
import emu.lunarcore.proto.SceneGroupRefreshInfoOuterClass.SceneGroupRefreshInfo;
import emu.lunarcore.proto.SceneGroupRefreshScNotifyOuterClass.SceneGroupRefreshScNotify;
import emu.lunarcore.proto.SceneGroupRefreshTypeOuterClass.SceneGroupRefreshType;
import emu.lunarcore.server.packet.BasePacket;
import emu.lunarcore.server.packet.CmdId;

public class PacketSceneGroupRefreshScNotify extends BasePacket {

    public PacketSceneGroupRefreshScNotify(Scene scene, GameEntity toAdd, GameEntity toRemove) {
        super(CmdId.SceneGroupRefreshScNotify);

        var group = SceneGroupRefreshInfo.newInstance()
                .setGroupRefreshTypeValue(3);

        if (toAdd != null) {
            group.setGroupId(toAdd.getGroupId());
            group.addRefreshEntity(SceneEntityRefreshInfo.newInstance().setAddEntity(toAdd.toSceneEntityProto()));
        } else if (toRemove != null) {
            group.setGroupId(toRemove.getGroupId());
            group.addRefreshEntity(SceneEntityRefreshInfo.newInstance().setDelEntity(toRemove.getEntityId()));
        }

        var data = SceneGroupRefreshScNotify.newInstance()
                .setFloorId(scene.getFloorId())
                .addGroupRefreshInfo(group);

        this.setData(data);
    }

    public PacketSceneGroupRefreshScNotify(Scene scene, Collection<? extends GameEntity> toAdd, Collection<? extends GameEntity> toRemove) {
        super(CmdId.SceneGroupRefreshScNotify);

        var group = SceneGroupRefreshInfo.newInstance()
                .setGroupRefreshTypeValue(3);

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
                .setFloorId(scene.getFloorId())
                .addGroupRefreshInfo(group);

        this.setData(data);
    }

    public PacketSceneGroupRefreshScNotify(Scene scene, GroupStateInfo info) {
        super(CmdId.SceneGroupRefreshScNotify);

        var group = SceneGroupRefreshInfo.newInstance()
                .setGroupId(info.getGroupId())
                .setState(info.getGroupState())
                .setGroupRefreshType(SceneGroupRefreshType.SCENE_GROUP_REFRESH_TYPE_LOADED);

        var data = SceneGroupRefreshScNotify.newInstance()
                .setFloorId(scene.getFloorId())
                .addGroupRefreshInfo(group);

        this.setData(data);
    }
}
