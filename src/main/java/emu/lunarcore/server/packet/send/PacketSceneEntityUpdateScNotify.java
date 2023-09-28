package emu.lunarcore.server.packet.send;

import emu.lunarcore.game.scene.entity.GameEntity;
import emu.lunarcore.proto.SceneEntityUpdateScNotifyOuterClass.SceneEntityUpdateScNotify;
import emu.lunarcore.server.packet.BasePacket;
import emu.lunarcore.server.packet.CmdId;

public class PacketSceneEntityUpdateScNotify extends BasePacket {

    public PacketSceneEntityUpdateScNotify(GameEntity toAdd) {
        super(CmdId.SceneEntityUpdateScNotify);

        var data = SceneEntityUpdateScNotify.newInstance()
                .addEntityList(toAdd.toSceneEntityProto());

        this.setData(data);
    }
}
