package emu.lunarcore.server.packet.recv;

import emu.lunarcore.proto.SceneEnterStageCsReqOuterClass.SceneEnterStageCsReq;
import emu.lunarcore.server.game.GameSession;
import emu.lunarcore.server.packet.CmdId;
import emu.lunarcore.server.packet.Opcodes;
import emu.lunarcore.server.packet.PacketHandler;

@Opcodes(CmdId.SceneEnterStageCsReq)
public class HandlerSceneEnterStageCsReq extends PacketHandler {
    @Override
    public void handle(GameSession session, byte[] data) throws Exception {
        var req = SceneEnterStageCsReq.parseFrom(data);
        
        session.getPlayer().getServer().getBattleService().startBattle(session.getPlayer(), req.getStageId());
    }
}
