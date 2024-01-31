package emu.lunarcore.server.packet.recv;

import emu.lunarcore.proto.HandleRogueCommonPendingActionCsReqOuterClass.HandleRogueCommonPendingActionCsReq;
import emu.lunarcore.server.game.GameSession;
import emu.lunarcore.server.packet.CmdId;
import emu.lunarcore.server.packet.Opcodes;
import emu.lunarcore.server.packet.PacketHandler;
//import emu.lunarcore.server.packet.send.PacketHandleRogueCommonPendingActionScRsp;

@Opcodes(CmdId.HandleRogueCommonPendingActionCsReq)
public class HandlerHandleRogueCommonPendingActionCsReq extends PacketHandler {
    @Override
    public void handle(GameSession session, byte[] data) throws Exception {
        var proto = HandleRogueCommonPendingActionCsReq.parseFrom(data);
        
        if (proto.hasBuffSelectResult()) {
            session.getPlayer().getRogueInstance().selectBuff(proto.getBuffSelectResult().getBuffId());
        } else if (proto.hasMiracleSelectResult()) {
            session.getPlayer().getRogueInstance().selectMiracle(proto.getMiracleSelectResult().getMiracleId());
        } else if (proto.hasBonusSelectResult()) {
            session.getPlayer().getRogueInstance().selectBonus(proto.getBonusSelectResult().getBonusId());
        } else if (proto.hasRollBuff()) {
            session.getPlayer().getRogueInstance().rollBuffSelect();
        }
    }
}
