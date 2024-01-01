package emu.lunarcore.server.packet.recv;

import emu.lunarcore.game.scene.entity.EntityProp;
import emu.lunarcore.proto.InteractPropCsReqOuterClass.InteractPropCsReq;
import emu.lunarcore.server.game.GameSession;
import emu.lunarcore.server.packet.CmdId;
import emu.lunarcore.server.packet.Opcodes;
import emu.lunarcore.server.packet.PacketHandler;
import emu.lunarcore.server.packet.send.PacketInteractPropScRsp;

@Opcodes(CmdId.InteractPropCsReq)
public class HandlerInteractPropCsReq extends PacketHandler {

    @Override
    public void handle(GameSession session, byte[] data) throws Exception {
        var req = InteractPropCsReq.parseFrom(data);
        
        EntityProp prop = session.getPlayer().interactWithProp(req.getInteractId(), req.getPropEntityId());
        
        session.send(new PacketInteractPropScRsp(prop));
    }

}
