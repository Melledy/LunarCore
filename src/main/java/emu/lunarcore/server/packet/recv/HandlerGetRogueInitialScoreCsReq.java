package emu.lunarcore.server.packet.recv;

import emu.lunarcore.server.game.GameSession;
import emu.lunarcore.server.packet.CmdId;
import emu.lunarcore.server.packet.Opcodes;
import emu.lunarcore.server.packet.PacketHandler;
import emu.lunarcore.server.packet.send.PacketGetRogueInitialScoreScRsp;

@Opcodes(CmdId.GetRogueInitialScoreCsReq)
public class HandlerGetRogueInitialScoreCsReq extends PacketHandler {

    @Override
    public void handle(GameSession session, byte[] data) throws Exception {
        session.send(new PacketGetRogueInitialScoreScRsp());
    }

}
