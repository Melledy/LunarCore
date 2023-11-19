package emu.lunarcore.server.packet.recv;

import emu.lunarcore.proto.MarkReadMailCsReqOuterClass.MarkReadMailCsReq;
import emu.lunarcore.server.game.GameSession;
import emu.lunarcore.server.packet.CmdId;
import emu.lunarcore.server.packet.Opcodes;
import emu.lunarcore.server.packet.PacketHandler;

@Opcodes(CmdId.MarkReadMailCsReq)
public class HandlerMarkReadMailCsReq extends PacketHandler {

    @Override
    public void handle(GameSession session, byte[] data) throws Exception {
        var req = MarkReadMailCsReq.parseFrom(data);
        
        session.getPlayer().getMailbox().readMail(req.getId());
        session.send(CmdId.MarkReadMailScRsp);
    }

}
