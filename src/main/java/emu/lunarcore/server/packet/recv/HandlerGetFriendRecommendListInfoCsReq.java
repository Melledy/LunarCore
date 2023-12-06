package emu.lunarcore.server.packet.recv;

import emu.lunarcore.server.game.GameSession;
import emu.lunarcore.server.packet.CmdId;
import emu.lunarcore.server.packet.Opcodes;
import emu.lunarcore.server.packet.PacketHandler;
import emu.lunarcore.server.packet.send.PacketGetFriendRecommendListInfoScRsp;

@Opcodes(CmdId.GetFriendRecommendListInfoCsReq)
public class HandlerGetFriendRecommendListInfoCsReq extends PacketHandler {

    @Override
    public void handle(GameSession session, byte[] data) throws Exception {
        var list = session.getServer().getRandomOnlinePlayers(10, session.getPlayer());
        session.send(new PacketGetFriendRecommendListInfoScRsp(list));
    }

}
