package emu.lunarcore.server.packet.recv;

import emu.lunarcore.proto.DeactivateFarmElementCsReqOuterClass.DeactivateFarmElementCsReq;
import emu.lunarcore.server.game.GameSession;
import emu.lunarcore.server.packet.CmdId;
import emu.lunarcore.server.packet.Opcodes;
import emu.lunarcore.server.packet.PacketHandler;
import emu.lunarcore.server.packet.send.PacketDeactivateFarmElementScRsp;

@Opcodes(CmdId.DeactivateFarmElementCsReq)
public class HandlerDeactivateFarmElementCsReq extends PacketHandler {

    @Override
    public void handle(GameSession session, byte[] data) throws Exception {
        var req = DeactivateFarmElementCsReq.parseFrom(data);
        session.send(new PacketDeactivateFarmElementScRsp(req.getEntityId()));
    }

}
