package emu.lunarcore.server.packet.recv;

import emu.lunarcore.proto.SceneCastSkillCsReqOuterClass.SceneCastSkillCsReq;
import emu.lunarcore.server.game.GameSession;
import emu.lunarcore.server.packet.CmdId;
import emu.lunarcore.server.packet.Opcodes;
import emu.lunarcore.server.packet.PacketHandler;
import emu.lunarcore.server.packet.send.PacketSceneCastSkillScRsp;

@Opcodes(CmdId.SceneCastSkillCsReq)
public class HandlerSceneCastSkillCsReq extends PacketHandler {

    @Override
    public void handle(GameSession session, byte[] header, byte[] data) throws Exception {
        var req = SceneCastSkillCsReq.parseFrom(data);

        if (req.hasAttackedEntityIdList()) {
            session.getServer().getBattleService().onBattleStart(session.getPlayer(), req.getAttackerId(), req.getAttackedEntityIdList());
        } else {
            session.send(new PacketSceneCastSkillScRsp());
        }
    }

}
