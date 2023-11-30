package emu.lunarcore.server.packet.recv;

import emu.lunarcore.server.game.GameSession;
import emu.lunarcore.server.packet.CmdId;
import emu.lunarcore.server.packet.Opcodes;
import emu.lunarcore.server.packet.PacketHandler;
import emu.lunarcore.server.packet.send.PacketDailyActiveInfoNotify;
import emu.lunarcore.server.packet.send.PacketBattlePassInfoNotify;
import emu.lunarcore.server.packet.send.PacketGetArchiveDataScRsp;
import emu.lunarcore.server.packet.send.PacketServerAnnounceNotify;

@Opcodes(CmdId.PlayerLoginFinishCsReq)
public class HandlerPlayerLoginFinishCsReq extends PacketHandler {

    @Override
    public void handle(GameSession session, byte[] data) throws Exception {
        session.send(CmdId.PlayerLoginFinishScRsp);
        session.send(new PacketBattlePassInfoNotify());
        session.send(new PacketServerAnnounceNotify());
        session.send(new PacketGetArchiveDataScRsp());
    }

}
