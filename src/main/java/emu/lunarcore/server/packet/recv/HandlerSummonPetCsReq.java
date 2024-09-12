package emu.lunarcore.server.packet.recv;

import emu.lunarcore.proto.SummonPetCsReqOuterClass.SummonPetCsReq;
import emu.lunarcore.server.game.GameSession;
import emu.lunarcore.server.packet.CmdId;
import emu.lunarcore.server.packet.Opcodes;
import emu.lunarcore.server.packet.PacketHandler;
import emu.lunarcore.server.packet.send.PacketCurPetChangedScNotify;

@Opcodes(CmdId.SummonPetCsReq)
public class HandlerSummonPetCsReq extends PacketHandler {

    @Override
    public void handle(GameSession session, byte[] data) throws Exception {
        var req = SummonPetCsReq.parseFrom(data);
        
        if (session.getPlayer().getUnlocks().getPets().contains(req.getSummonedPetId())) {
            session.getPlayer().setPetId(req.getSummonedPetId());
            session.getPlayer().save();
            session.send(new PacketCurPetChangedScNotify(session.getPlayer()));
        }
        
        session.send(CmdId.SummonPetScRsp);
    }

}
