package emu.lunarcore.server.packet.recv;

import emu.lunarcore.proto.StartChallengeCsReqOuterClass.StartChallengeCsReq;
import emu.lunarcore.server.game.GameSession;
import emu.lunarcore.server.packet.CmdId;
import emu.lunarcore.server.packet.Opcodes;
import emu.lunarcore.server.packet.PacketHandler;

@Opcodes(CmdId.StartChallengeCsReq)
public class HandlerStartChallengeCsReq extends PacketHandler {

    @Override
    public void handle(GameSession session, byte[] data) throws Exception {
        var req = StartChallengeCsReq.parseFrom(data);
        var storyBuffs = req.getMutableStoryInfo().getMutableStoryBuffInfo();
        
        session.getPlayer().getChallengeManager().startChallenge(req.getChallengeId(), storyBuffs);
    }

}
