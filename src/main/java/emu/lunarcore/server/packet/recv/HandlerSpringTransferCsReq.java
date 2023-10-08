package emu.lunarcore.server.packet.recv;

import emu.lunarcore.game.scene.entity.EntityProp;
import emu.lunarcore.game.scene.entity.GameEntity;
import emu.lunarcore.proto.SpringTransferCsReqOuterClass.SpringTransferCsReq;
import emu.lunarcore.server.game.GameSession;
import emu.lunarcore.server.packet.CmdId;
import emu.lunarcore.server.packet.Opcodes;
import emu.lunarcore.server.packet.PacketHandler;

@Opcodes(CmdId.SpringTransferCsReq)
public class HandlerSpringTransferCsReq extends PacketHandler {

    @Override
    public void handle(GameSession session, byte[] header, byte[] data) throws Exception {
        var req = SpringTransferCsReq.parseFrom(data);
        
        GameEntity entity = session.getPlayer().getScene().getEntityById(req.getPropEntityId());
        if (entity != null && entity instanceof EntityProp prop) {
            var anchor = session.getPlayer().getScene().getFloorInfo().getAnchorInfo(
                    prop.getPropInfo().getAnchorGroupID(), 
                    prop.getPropInfo().getAnchorID()
            );
            if (anchor != null) {
                session.getPlayer().moveTo(anchor.getPos());
            }
        }
        
        session.send(CmdId.SpringTransferScRsp);
    }

}
