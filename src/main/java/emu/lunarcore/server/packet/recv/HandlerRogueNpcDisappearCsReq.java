package emu.lunarcore.server.packet.recv;

import emu.lunarcore.game.scene.entity.GameEntity;
import emu.lunarcore.proto.RogueNpcDisappearCsReqOuterClass.RogueNpcDisappearCsReq;
import emu.lunarcore.server.game.GameSession;
import emu.lunarcore.server.packet.CmdId;
import emu.lunarcore.server.packet.Opcodes;
import emu.lunarcore.server.packet.PacketHandler;
import emu.lunarcore.server.packet.send.PacketSceneGroupRefreshScNotify;

@Opcodes(CmdId.RogueNpcDisappearCsReq)
public class HandlerRogueNpcDisappearCsReq extends PacketHandler {
    @Override
    public void handle(GameSession session, byte[] data) throws Exception {
        var req = RogueNpcDisappearCsReq.parseFrom(data);
        
        GameEntity entity = session.getPlayer().getScene().getEntityById(req.getEntityId());
        
        if (entity != null) {
            session.send(CmdId.RogueNpcDisappearScRsp);
            session.send(new PacketSceneGroupRefreshScNotify(null, entity));
        }
    }
}
