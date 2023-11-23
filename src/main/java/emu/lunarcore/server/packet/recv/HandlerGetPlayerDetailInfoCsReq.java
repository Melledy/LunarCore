package emu.lunarcore.server.packet.recv;

import emu.lunarcore.game.player.Player;
import emu.lunarcore.proto.GetPlayerDetailInfoCsReqOuterClass.GetPlayerDetailInfoCsReq;
import emu.lunarcore.server.game.GameSession;
import emu.lunarcore.server.packet.CmdId;
import emu.lunarcore.server.packet.Opcodes;
import emu.lunarcore.server.packet.PacketHandler;
import emu.lunarcore.server.packet.send.PacketGetPlayerDetailInfoScRsp;

@Opcodes(CmdId.GetPlayerDetailInfoCsReq)
public class HandlerGetPlayerDetailInfoCsReq extends PacketHandler {

    @SuppressWarnings("unused")
    @Override
    public void handle(GameSession session, byte[] data) throws Exception {
        var req = GetPlayerDetailInfoCsReq.parseFrom(data);
        
        Player player = session.getServer().getPlayerByUid(req.getUid(), true);
        session.send(new PacketGetPlayerDetailInfoScRsp(player));
    }

}
