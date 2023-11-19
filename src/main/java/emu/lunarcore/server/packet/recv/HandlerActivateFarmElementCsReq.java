package emu.lunarcore.server.packet.recv;

import emu.lunarcore.proto.ActivateFarmElementCsReqOuterClass.ActivateFarmElementCsReq;
import emu.lunarcore.server.game.GameSession;
import emu.lunarcore.server.packet.CmdId;
import emu.lunarcore.server.packet.Opcodes;
import emu.lunarcore.server.packet.PacketHandler;

@Opcodes(CmdId.ActivateFarmElementCsReq)
public class HandlerActivateFarmElementCsReq extends PacketHandler {

    @Override
    public void handle(GameSession session, byte[] data) throws Exception {
        var req = ActivateFarmElementCsReq.parseFrom(data);
        
        session.getPlayer().getScene().activateFarmElement(req.getEntityId(), req.getWorldLevel());
    }

}
