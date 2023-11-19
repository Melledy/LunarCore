package emu.lunarcore.server.packet.recv;

import emu.lunarcore.proto.DelMailCsReqOuterClass.DelMailCsReq;
import emu.lunarcore.server.game.GameSession;
import emu.lunarcore.server.packet.CmdId;
import emu.lunarcore.server.packet.Opcodes;
import emu.lunarcore.server.packet.PacketHandler;
import emu.lunarcore.server.packet.send.PacketDelMailScRsp;
import it.unimi.dsi.fastutil.ints.IntList;

@Opcodes(CmdId.DelMailCsReq)
public class HandlerDelMailCsReq extends PacketHandler {

    @Override
    public void handle(GameSession session, byte[] data) throws Exception {
        var req = DelMailCsReq.parseFrom(data);
        
        IntList deleted = session.getPlayer().getMailbox().deleteMail(req.getIdList());
        
        session.send(new PacketDelMailScRsp(deleted));
    }

}
