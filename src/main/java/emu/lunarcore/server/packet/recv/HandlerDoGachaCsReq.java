package emu.lunarcore.server.packet.recv;

import emu.lunarcore.proto.DoGachaCsReqOuterClass.DoGachaCsReq;
import emu.lunarcore.server.game.GameSession;
import emu.lunarcore.server.packet.CmdId;
import emu.lunarcore.server.packet.Opcodes;
import emu.lunarcore.server.packet.PacketHandler;

@Opcodes(CmdId.DoGachaCsReq)
public class HandlerDoGachaCsReq extends PacketHandler {

    @Override
    public void handle(GameSession session, byte[] data) throws Exception {
        var req = DoGachaCsReq.parseFrom(data);

        session.getServer().getGachaService().doPulls(session.getPlayer(), req.getGachaId(), req.getGachaNum());
    }

}
