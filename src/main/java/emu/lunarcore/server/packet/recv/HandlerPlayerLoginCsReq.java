package emu.lunarcore.server.packet.recv;

import emu.lunarcore.server.game.GameSession;
import emu.lunarcore.server.packet.CmdId;
import emu.lunarcore.server.packet.Opcodes;
import emu.lunarcore.server.packet.PacketHandler;
import emu.lunarcore.server.packet.SessionState;
import emu.lunarcore.server.packet.send.*;

@Opcodes(CmdId.PlayerLoginCsReq)
public class HandlerPlayerLoginCsReq extends PacketHandler {

    @Override
    public void handle(GameSession session, byte[] data) throws Exception {
        // Set session flag
        session.setState(SessionState.ACTIVE);

        // Send packets
        session.send(new PacketPlayerLoginScRsp(session));
        session.send(new PacketUpdateFeatureSwitchScNotify());
        session.send(new PacketStaminaInfoScNotify(session.getPlayer()));
        session.send(new PacketBattlePassInfoNotify());
        session.send(new PacketContentPackageGetDataScRsp());
        session.send(CmdId.SyncServerSceneChangeNotify);
        session.send(CmdId.SyncTurnFoodNotify);
        session.send(CmdId.RaidInfoNotify);
        session.send(CmdId.ComposeLimitNumCompleteNotify);
        session.send(CmdId.NewAssistHistoryNotify);

    }

}
