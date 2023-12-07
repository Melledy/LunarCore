package emu.lunarcore.server.packet.recv;

import emu.lunarcore.server.game.GameSession;
import emu.lunarcore.server.packet.CmdId;
import emu.lunarcore.server.packet.Opcodes;
import emu.lunarcore.server.packet.PacketHandler;

@Opcodes(CmdId.GetArchiveDataCsReq)
public class HandlerGetArchiveDataCsReq extends PacketHandler {

    @Override
    public void handle(GameSession session, byte[] data) throws Exception {
        // TODO The client does not send this packet right now to the server so we send it in HandlerPlayerLoginFinishCsReq instead
        session.send(CmdId.GetArchiveDataScRsp);
    }

}
