package emu.lunarcore.server.packet.recv;

import emu.lunarcore.proto.StartChallengePeakCsReqOuterClass.StartChallengePeakCsReq;
import emu.lunarcore.server.game.GameSession;
import emu.lunarcore.server.packet.CmdId;
import emu.lunarcore.server.packet.Opcodes;
import emu.lunarcore.server.packet.PacketHandler;

@Opcodes(CmdId.StartChallengePeakCsReq)
public class HandlerStartChallengePeakCsReq extends PacketHandler {

    @Override
    public void handle(GameSession session, byte[] data) throws Exception {
        var req = StartChallengePeakCsReq.parseFrom(data);
        
        session.getPlayer().getChallengeManager().startChallengePeak(req.getChallengePeakId());
        session.send(CmdId.StartChallengePeakScRsp);
    }

}
