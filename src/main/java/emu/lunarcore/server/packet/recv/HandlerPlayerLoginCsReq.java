package emu.lunarcore.server.packet.recv;

import emu.lunarcore.server.game.GameSession;
import emu.lunarcore.server.packet.CmdId;
import emu.lunarcore.server.packet.Opcodes;
import emu.lunarcore.server.packet.PacketHandler;
import emu.lunarcore.server.packet.SessionState;
import emu.lunarcore.server.packet.send.PacketPlayerLoginScRsp;
import emu.lunarcore.server.packet.send.PacketStaminaInfoScNotify;

@Opcodes(CmdId.PlayerLoginCsReq)
public class HandlerPlayerLoginCsReq extends PacketHandler {

    @Override
    public void handle(GameSession session, byte[] data) throws Exception {
        // Set session flag
        session.setState(SessionState.ACTIVE);

        // Send packets
        session.send(new PacketPlayerLoginScRsp(session));
        session.send(new PacketStaminaInfoScNotify(session.getPlayer()));
    }

}
