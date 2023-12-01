package emu.lunarcore.server.packet.recv;

import emu.lunarcore.server.game.GameSession;
import emu.lunarcore.server.packet.CmdId;
import emu.lunarcore.server.packet.Opcodes;
import emu.lunarcore.server.packet.PacketHandler;
import emu.lunarcore.server.packet.send.PacketGetJukeboxDataScRsp;

@Opcodes(CmdId.GetJukeboxDataCsReq)
public class HandlerGetJukeboxDataCsReq extends PacketHandler {

    @Override
    public void handle(GameSession session, byte[] data) throws Exception {
        // var req = GetJukeboxDataCsReq.parseFrom(data);
        session.send(new PacketGetJukeboxDataScRsp(session.getPlayer()));
    }

}
