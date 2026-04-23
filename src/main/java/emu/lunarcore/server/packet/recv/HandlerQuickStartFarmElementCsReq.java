package emu.lunarcore.server.packet.recv;

import emu.lunarcore.data.GameData;
import emu.lunarcore.game.battle.Battle;
import emu.lunarcore.proto.QuickStartFarmElementCsReqOuterClass.QuickStartFarmElementCsReq;
import emu.lunarcore.server.game.GameSession;
import emu.lunarcore.server.packet.CmdId;
import emu.lunarcore.server.packet.Opcodes;
import emu.lunarcore.server.packet.PacketHandler;
import emu.lunarcore.server.packet.send.PacketQuickStartFarmElementScRsp;

@Opcodes(CmdId.QuickStartFarmElementCsReq)
public class HandlerQuickStartFarmElementCsReq extends PacketHandler {

    @Override
    public void handle(GameSession session, byte[] data) throws Exception {
        var req = QuickStartFarmElementCsReq.parseFrom(data);

        var excel = GameData.getFarmElementExcelMap().get(req.getFarmElementId(), req.getWorldLevel());
        if (excel == null) {
            session.send(new PacketQuickStartFarmElementScRsp(null));
            return;
        }

        Battle battle = session.getServer().getBattleService().startBattle(
                session.getPlayer(),
                excel.getStageID(),
                req.getWorldLevel()
                );

        if (battle != null) {
            battle.setMappingInfoId(req.getFarmElementId());
            battle.setStaminaCost(excel.getStaminaCost());
            battle.setWorldLevel(req.getWorldLevel());
        }

        session.send(new PacketQuickStartFarmElementScRsp(battle));
    }

}
