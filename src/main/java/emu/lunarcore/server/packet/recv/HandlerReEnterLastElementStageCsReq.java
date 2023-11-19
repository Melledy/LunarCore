package emu.lunarcore.server.packet.recv;

import emu.lunarcore.proto.ReEnterLastElementStageCsReqOuterClass.ReEnterLastElementStageCsReq;
import emu.lunarcore.server.game.GameSession;
import emu.lunarcore.server.packet.CmdId;
import emu.lunarcore.server.packet.Opcodes;
import emu.lunarcore.server.packet.PacketHandler;

@Opcodes(CmdId.ReEnterLastElementStageCsReq)
public class HandlerReEnterLastElementStageCsReq extends PacketHandler {

    @Override
    public void handle(GameSession session, byte[] data) throws Exception {
        var req = ReEnterLastElementStageCsReq.parseFrom(data);
        
        session.getServer().getBattleService().reEnterBattle(session.getPlayer(), req.getStageId());
    }

}
