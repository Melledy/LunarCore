package emu.lunarcore.server.packet.recv;

import emu.lunarcore.server.game.GameSession;
import emu.lunarcore.server.packet.CmdId;
import emu.lunarcore.server.packet.Opcodes;
import emu.lunarcore.server.packet.PacketHandler;
import emu.lunarcore.server.packet.send.PacketGetGachaCeilingScRsp;
import emu.lunarcore.proto.GetGachaCeilingCsReqOuterClass.GetGachaCeilingCsReq;

@Opcodes(CmdId.GetGachaCeilingCsReq)
public class HandlerGetGachaCeilingCsReq extends PacketHandler {

    @Override
    public void handle(GameSession session, byte[] data) throws Exception {
        var req = GetGachaCeilingCsReq.parseFrom(data);
        session.send(new PacketGetGachaCeilingScRsp(req.getUnkfield()));
    }

}
