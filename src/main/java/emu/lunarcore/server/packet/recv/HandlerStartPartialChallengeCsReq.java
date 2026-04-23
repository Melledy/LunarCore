package emu.lunarcore.server.packet.recv;

import emu.lunarcore.proto.StartPartialChallengeCsReqOuterClass.StartPartialChallengeCsReq;
import emu.lunarcore.server.game.GameSession;
import emu.lunarcore.server.packet.CmdId;
import emu.lunarcore.server.packet.Opcodes;
import emu.lunarcore.server.packet.PacketHandler;

@Opcodes(CmdId.StartPartialChallengeCsReq)
public class HandlerStartPartialChallengeCsReq extends PacketHandler {

    @Override
    public void handle(GameSession session, byte[] data) throws Exception {
        var req = StartPartialChallengeCsReq.parseFrom(data);

        var manager = session.getPlayer().getChallengeManager();

        var lineup1 = manager.getLastLineup1();
        var lineup2 = manager.getLastLineup2();
        var firstHalfBuff = manager.getLastFirstHalfBuff();
        var secondHalfBuff = manager.getLastSecondHalfBuff();

        var isFirsthalf = req.getIsFirstHalf();

        if (req.hasBuffId() && req.getBuffId() != 0) {
            if (isFirsthalf) {
                firstHalfBuff = req.getBuffId();
            } else {
                secondHalfBuff = req.getBuffId();
            }
        }

        manager.startChallenge(req.getChallengeId(), lineup1, lineup2, firstHalfBuff, secondHalfBuff);
    }

}
