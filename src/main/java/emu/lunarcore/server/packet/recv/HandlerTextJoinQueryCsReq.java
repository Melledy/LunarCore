package emu.lunarcore.server.packet.recv;

import emu.lunarcore.server.game.GameSession;
import emu.lunarcore.server.packet.CmdId;
import emu.lunarcore.server.packet.Opcodes;
import emu.lunarcore.server.packet.PacketHandler;
import emu.lunarcore.server.packet.send.PacketTextJoinQueryScRsp;
import emu.lunarcore.proto.TextJoinQueryCsReqOuterClass.TextJoinQueryCsReq;

@Opcodes(CmdId.TextJoinQueryCsReq)
public class HandlerTextJoinQueryCsReq extends PacketHandler {

    @Override
    public void handle(GameSession session, byte[] data) throws Exception {
        var req = TextJoinQueryCsReq.parseFrom(data);
        session.send(new PacketTextJoinQueryScRsp(session.getPlayer(), req.getTextJoinIdList().toArray()));
    }

}
