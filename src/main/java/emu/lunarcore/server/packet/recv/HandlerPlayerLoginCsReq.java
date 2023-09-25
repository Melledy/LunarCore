package emu.lunarcore.server.packet.recv;

import emu.lunarcore.server.game.GameSession;
import emu.lunarcore.server.packet.CmdId;
import emu.lunarcore.server.packet.Opcodes;
import emu.lunarcore.server.packet.PacketHandler;
import emu.lunarcore.server.packet.SessionState;
import emu.lunarcore.server.packet.send.PacketPlayerLoginScRsp;

@Opcodes(CmdId.PlayerLoginCsReq)
public class HandlerPlayerLoginCsReq extends PacketHandler {

    @Override
    public void handle(GameSession session, byte[] header, byte[] data) throws Exception {
        // Set session flag
        session.setState(SessionState.ACTIVE);

        // Send
        session.send(new PacketPlayerLoginScRsp(session));
    }

}
