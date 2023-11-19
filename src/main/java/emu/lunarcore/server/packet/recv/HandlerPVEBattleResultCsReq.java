package emu.lunarcore.server.packet.recv;

import emu.lunarcore.game.battle.Battle;
import emu.lunarcore.proto.PVEBattleResultCsReqOuterClass.PVEBattleResultCsReq;
import emu.lunarcore.server.game.GameSession;
import emu.lunarcore.server.packet.CmdId;
import emu.lunarcore.server.packet.Opcodes;
import emu.lunarcore.server.packet.PacketHandler;
import emu.lunarcore.server.packet.send.PacketPVEBattleResultScRsp;

@Opcodes(CmdId.PVEBattleResultCsReq)
public class HandlerPVEBattleResultCsReq extends PacketHandler {

    @Override
    public void handle(GameSession session, byte[] data) throws Exception {
        var req = PVEBattleResultCsReq.parseFrom(data);

        Battle battle = session.getServer().getBattleService().finishBattle(
                session.getPlayer(),
                req.getEndStatus(),
                req.getStt()
        );

        if (battle != null) {
            session.send(new PacketPVEBattleResultScRsp(req, battle));
        } else {
            session.send(new PacketPVEBattleResultScRsp());
        }
    }

}
