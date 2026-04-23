package emu.lunarcore.server.packet.recv;

import emu.lunarcore.game.trial.TrialInstance;
import emu.lunarcore.server.game.GameSession;
import emu.lunarcore.server.packet.CmdId;
import emu.lunarcore.server.packet.Opcodes;
import emu.lunarcore.server.packet.PacketHandler;
import emu.lunarcore.server.packet.send.PacketCurTrialActivityScNotify;

@Opcodes(CmdId.LeaveTrialActivityCsReq)
public class HandlerLeaveTrialActivityCsReq extends PacketHandler {

    @Override
    public void handle(GameSession session, byte[] data) throws Exception {
        // Get trial instance
        TrialInstance trial = session.getPlayer().getInstance(TrialInstance.class);
        if (trial != null) {
            // Send player back to where they were
            session.getPlayer().getLineupManager().setCurrentExtraLineup(0, false);
            session.getPlayer().loadScene(trial.getLastLocation(), true);
            session.getPlayer().sendPacket(new PacketCurTrialActivityScNotify(trial));
            // Reset instance
            session.getPlayer().setInstance(null);
        }

        // Leave
        session.send(CmdId.LeaveTrialActivityScRsp);
    }

}
