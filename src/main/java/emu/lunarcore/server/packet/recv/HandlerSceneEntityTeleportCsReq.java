package emu.lunarcore.server.packet.recv;

import emu.lunarcore.proto.EntityMotionOuterClass.EntityMotion;
import emu.lunarcore.proto.SceneEntityTeleportCsReqOuterClass.SceneEntityTeleportCsReq;
import emu.lunarcore.server.game.GameSession;
import emu.lunarcore.server.packet.CmdId;
import emu.lunarcore.server.packet.Opcodes;
import emu.lunarcore.server.packet.PacketHandler;
import emu.lunarcore.server.packet.send.PacketSceneEntityTeleportScRsp;
import emu.lunarcore.util.Position;

@Opcodes(CmdId.SceneEntityTeleportCsReq)
public class HandlerSceneEntityTeleportCsReq extends PacketHandler {

    @Override
    public void handle(GameSession session, byte[] data) throws Exception {
        var req = SceneEntityTeleportCsReq.parseFrom(data);

        EntityMotion motion = req.getEntityMotion();

        if (session.getPlayer().getScene().getAvatarEntityIds().contains(motion.getEntityId())) {
            var vec = motion.getMotion().getPos();
            var vecRot = motion.getMotion().getRot();
            session.getPlayer().moveTo(new Position(vec.getX(), vec.getY(), vec.getZ()), new Position(0, vecRot.getY(), 0));
        }

        session.send(new PacketSceneEntityTeleportScRsp(motion));
    }

}