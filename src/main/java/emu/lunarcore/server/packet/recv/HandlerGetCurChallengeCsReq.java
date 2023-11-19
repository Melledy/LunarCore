package emu.lunarcore.server.packet.recv;

import emu.lunarcore.server.game.GameSession;
import emu.lunarcore.server.packet.CmdId;
import emu.lunarcore.server.packet.Opcodes;
import emu.lunarcore.server.packet.PacketHandler;
import emu.lunarcore.server.packet.send.PacketGetCurChallengeScRsp;

@Opcodes(CmdId.GetCurChallengeCsReq)
public class HandlerGetCurChallengeCsReq extends PacketHandler {

    @Override
    public void handle(GameSession session, byte[] data) throws Exception {
        // Send packet first
        session.send(new PacketGetCurChallengeScRsp(session.getPlayer()));
        // Update challenge details for client
        if (session.getPlayer().getChallengeInstance() != null) {
            session.getPlayer().getChallengeInstance().onUpdate();
        }
    }

}
