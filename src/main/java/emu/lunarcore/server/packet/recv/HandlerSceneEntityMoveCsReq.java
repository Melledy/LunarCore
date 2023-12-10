package emu.lunarcore.server.packet.recv;

import emu.lunarcore.proto.SceneEntityMoveCsReqOuterClass.SceneEntityMoveCsReq;
import emu.lunarcore.server.game.GameSession;
import emu.lunarcore.server.packet.CmdId;
import emu.lunarcore.server.packet.Opcodes;
import emu.lunarcore.server.packet.PacketHandler;

@Opcodes(CmdId.SceneEntityMoveCsReq)
public class HandlerSceneEntityMoveCsReq extends PacketHandler {

    @Override
    public void handle(GameSession session, byte[] data) throws Exception {
        var req = SceneEntityMoveCsReq.parseFrom(data);

        for (var entityMotion : req.getEntityMotionList()) {
            // Update player position
            if (session.getPlayer().getScene().getAvatarEntityIds().contains(entityMotion.getEntityId())) {
                var vec = entityMotion.getMotion().getPos();
                session.getPlayer().getPos().set(vec.getX(), vec.getY(), vec.getZ());
                session.getPlayer().onMove();
            }
        }

        session.send(CmdId.SceneEntityMoveScRsp);
    }

}
