package emu.lunarcore.server.packet.recv;

import emu.lunarcore.GameConstants;
import emu.lunarcore.game.enums.PlaneType;
import emu.lunarcore.server.game.GameSession;
import emu.lunarcore.server.packet.CmdId;
import emu.lunarcore.server.packet.Opcodes;
import emu.lunarcore.server.packet.PacketHandler;

@Opcodes(CmdId.LeaveChallengeCsReq)
public class HandlerLeaveChallengeCsReq extends PacketHandler {

    @Override
    public void handle(GameSession session, byte[] data) throws Exception {
        // Make sure client is in a challenge scene
        if (session.getPlayer().getScene() != null && session.getPlayer().getScene().getPlaneType() == PlaneType.Challenge) {
            // As of 1.5.0, the server now has to handle the player leaving battle too
            session.getPlayer().forceQuitBattle();
            
            // Get entry id
            int leaveEntryId = GameConstants.CHALLENGE_ENTRANCE;
            if (session.getPlayer().getScene().getLeaveEntryId() != 0) {
                leaveEntryId = session.getPlayer().getScene().getLeaveEntryId();
            }
            
            // Leave scene
            session.getPlayer().getLineupManager().setCurrentExtraLineup(0, false);
            session.getPlayer().enterScene(leaveEntryId, 0, true);
        }
        
        // Send rsp packet to keep the client happy
        session.send(CmdId.LeaveChallengeScRsp);
    }

}