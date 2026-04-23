package emu.lunarcore.server.packet.recv;

import emu.lunarcore.proto.SetChallengePeakMobLineupAvatarCsReqOuterClass.SetChallengePeakMobLineupAvatarCsReq;
import emu.lunarcore.server.game.GameSession;
import emu.lunarcore.server.packet.CmdId;
import emu.lunarcore.server.packet.Opcodes;
import emu.lunarcore.server.packet.PacketHandler;

@Opcodes(CmdId.SetChallengePeakMobLineupAvatarCsReq)
public class HandlerSetChallengePeakMobLineupAvatarCsReq extends PacketHandler {

    @Override
    public void handle(GameSession session, byte[] data) throws Exception {
        var req = SetChallengePeakMobLineupAvatarCsReq.parseFrom(data);
        
        session.getPlayer().getChallengeManager().setChallengePeakMobLineupAvatars(
            req.getChallengePeakGroupId(),
            req.getLineupList()
        );
        
        session.send(CmdId.SetChallengePeakMobLineupAvatarScRsp);
    }

}
