package emu.lunarcore.server.packet.recv;

import emu.lunarcore.proto.RecallPetCsReqOuterClass.RecallPetCsReq;
import emu.lunarcore.server.game.GameSession;
import emu.lunarcore.server.packet.CmdId;
import emu.lunarcore.server.packet.Opcodes;
import emu.lunarcore.server.packet.PacketHandler;
import emu.lunarcore.server.packet.send.PacketRecallPetScRsp;

@Opcodes(CmdId.RecallPetCsReq)
public class HandlerRecallPetCsReq extends PacketHandler {

    @Override
    public void handle(GameSession session, byte[] data) throws Exception {
        var req = RecallPetCsReq.parseFrom(data);

        session.getPlayer().setPetId(0);
        session.getPlayer().save();

        session.send(new PacketRecallPetScRsp(req.getSummonedPetId()));
    }

}