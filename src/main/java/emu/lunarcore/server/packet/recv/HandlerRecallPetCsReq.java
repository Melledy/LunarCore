package emu.lunarcore.server.packet.recv;

import emu.lunarcore.server.game.GameSession;
import emu.lunarcore.server.packet.CmdId;
import emu.lunarcore.server.packet.Opcodes;
import emu.lunarcore.server.packet.PacketHandler;
import emu.lunarcore.server.packet.send.PacketCurPetChangedScNotify;

@Opcodes(CmdId.RecallPetCsReq)
public class HandlerRecallPetCsReq extends PacketHandler {

    @Override
    public void handle(GameSession session, byte[] data) throws Exception {
        session.getPlayer().setPetId(0);
        session.getPlayer().save();
        
        session.send(new PacketCurPetChangedScNotify(session.getPlayer()));
        session.send(CmdId.RecallPetScRsp);
    }

}
