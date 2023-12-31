package emu.lunarcore.server.packet.recv;

import emu.lunarcore.GameConstants;
import emu.lunarcore.server.game.GameSession;
import emu.lunarcore.server.packet.CmdId;
import emu.lunarcore.server.packet.Opcodes;
import emu.lunarcore.server.packet.PacketHandler;
import emu.lunarcore.server.packet.send.PacketGetRogueInfoScRsp;

@Opcodes(CmdId.GetRogueInfoCsReq)
public class HandlerGetRogueInfoCsReq extends PacketHandler {

    @Override
    public void handle(GameSession session, byte[] data) throws Exception {
        if (GameConstants.ENABLE_ROGUE) {
            session.send(new PacketGetRogueInfoScRsp(session.getPlayer()));
        } else {
            session.send(CmdId.GetRogueInfoScRsp);
        }
    }

}
