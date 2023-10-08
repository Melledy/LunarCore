package emu.lunarcore.server.packet.recv;

import emu.lunarcore.server.game.GameSession;
import emu.lunarcore.server.packet.CmdId;
import emu.lunarcore.server.packet.Opcodes;
import emu.lunarcore.server.packet.PacketHandler;

@Opcodes(CmdId.LeaveChallengeCsReq)
public class HandlerLeaveChallengeCsReq extends PacketHandler {

    @Override
    public void handle(GameSession session, byte[] header, byte[] data) throws Exception {
        // TODO make sure client is in a challenge mode map
        session.getPlayer().getLineupManager().setCurrentExtraLineup(0, false);
        session.getPlayer().enterScene(100000103, 0, true);
        session.send(CmdId.LeaveChallengeScRsp);
    }

}