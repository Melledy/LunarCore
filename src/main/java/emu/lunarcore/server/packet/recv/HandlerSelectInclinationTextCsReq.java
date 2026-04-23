package emu.lunarcore.server.packet.recv;

import emu.lunarcore.proto.SelectInclinationTextCsReqOuterClass.SelectInclinationTextCsReq;
import emu.lunarcore.server.game.GameSession;
import emu.lunarcore.server.packet.CmdId;
import emu.lunarcore.server.packet.Opcodes;
import emu.lunarcore.server.packet.PacketHandler;
import emu.lunarcore.server.packet.send.PacketSelectInclinationTextScRsp;

@Opcodes(CmdId.SelectInclinationTextCsReq)
public class HandlerSelectInclinationTextCsReq extends PacketHandler {
    @Override
    public void handle(GameSession session, byte[] data) throws Exception {
        var req = SelectInclinationTextCsReq.parseFrom(data);

        session.send(new PacketSelectInclinationTextScRsp(req.getInclinationTextId()));
    }
}