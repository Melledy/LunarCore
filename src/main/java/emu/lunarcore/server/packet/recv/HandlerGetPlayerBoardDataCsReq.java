package emu.lunarcore.server.packet.recv;

import emu.lunarcore.server.game.GameSession;
import emu.lunarcore.server.packet.CmdId;
import emu.lunarcore.server.packet.Opcodes;
import emu.lunarcore.server.packet.PacketHandler;
import emu.lunarcore.server.packet.send.PacketGetPlayerBoardDataScRsp;
import emu.lunarcore.server.packet.send.PacketBattlePassInfoNotify;

@Opcodes(CmdId.GetPlayerBoardDataCsReq)
public class HandlerGetPlayerBoardDataCsReq extends PacketHandler {

    @Override
    public void handle(GameSession session, byte[] data) throws Exception {
        session.send(new PacketGetPlayerBoardDataScRsp(session.getPlayer()));
        session.send(new PacketBattlePassInfoNotify());
    }

}
