package emu.lunarcore.server.packet.send;

import emu.lunarcore.proto.EntityMotionOuterClass.EntityMotion;
import emu.lunarcore.proto.SceneEntityTeleportScRspOuterClass.SceneEntityTeleportScRsp;
import emu.lunarcore.server.packet.BasePacket;
import emu.lunarcore.server.packet.CmdId;

public class PacketSceneEntityTeleportScRsp extends BasePacket {

    public PacketSceneEntityTeleportScRsp(EntityMotion motion) {
        super(CmdId.SceneEntityTeleportScRsp);

        var data = SceneEntityTeleportScRsp.newInstance();

        data.setEntityMotion(motion);

        this.setData(data);
    }
}