package emu.lunarcore.server.packet.recv;

import emu.lunarcore.game.battle.Battle;
import emu.lunarcore.proto.QuickStartCocoonStageCsReqOuterClass.QuickStartCocoonStageCsReq;
import emu.lunarcore.server.game.GameSession;
import emu.lunarcore.server.packet.CmdId;
import emu.lunarcore.server.packet.Opcodes;
import emu.lunarcore.server.packet.PacketHandler;
import emu.lunarcore.server.packet.send.PacketQuickStartCocoonStageScRsp;

@Opcodes(CmdId.QuickStartCocoonStageCsReq)
public class HandlerQuickStartCocoonStageCsReq extends PacketHandler {

    @Override
    public void handle(GameSession session, byte[] data) throws Exception {
        var req = QuickStartCocoonStageCsReq.parseFrom(data);
        
        Battle battle = session.getServer().getBattleService().startCocoon(
                session.getPlayer(),
                req.getCocoonId(),
                req.getWorldLevel(),
                req.getWaveCount()
                );

        session.send(new PacketQuickStartCocoonStageScRsp(battle));
    }

}
