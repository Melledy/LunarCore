package emu.lunarcore.server.packet.recv;

import emu.lunarcore.game.trial.TrialInstance;
import emu.lunarcore.proto.StartTrialActivityCsReqOuterClass.StartTrialActivityCsReq;
import emu.lunarcore.server.game.GameSession;
import emu.lunarcore.server.packet.CmdId;
import emu.lunarcore.server.packet.Opcodes;
import emu.lunarcore.server.packet.PacketHandler;
import emu.lunarcore.server.packet.send.PacketStartTrialActivityScRsp;

@Opcodes(CmdId.StartTrialActivityCsReq)
public class HandlerStartTrialActivityCsReq extends PacketHandler {

    @Override
    public void handle(GameSession session, byte[] data) throws Exception {
        var req = StartTrialActivityCsReq.parseFrom(data);

        TrialInstance trial = session.getPlayer().getTrialManager().startTrial(req.getStageId());
        session.send(new PacketStartTrialActivityScRsp(trial));
    }

}
