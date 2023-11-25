package emu.lunarcore.server.packet.recv;

import emu.lunarcore.proto.RankUpAvatarCsReqOuterClass.RankUpAvatarCsReq;
import emu.lunarcore.server.game.GameSession;
import emu.lunarcore.server.packet.CmdId;
import emu.lunarcore.server.packet.Opcodes;
import emu.lunarcore.server.packet.PacketHandler;

@Opcodes(CmdId.RankUpAvatarCsReq)
public class HandlerRankUpAvatarCsReq extends PacketHandler {

    @Override
    public void handle(GameSession session, byte[] data) throws Exception {
        var req = RankUpAvatarCsReq.parseFrom(data);

        session.getServer().getInventoryService().rankUpAvatar(session.getPlayer(), req.getBaseAvatarId());
        session.send(CmdId.RankUpAvatarScRsp);
    }

}
