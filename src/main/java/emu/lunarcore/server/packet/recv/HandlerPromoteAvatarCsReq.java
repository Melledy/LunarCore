package emu.lunarcore.server.packet.recv;

import emu.lunarcore.proto.PromoteAvatarCsReqOuterClass.PromoteAvatarCsReq;
import emu.lunarcore.server.game.GameSession;
import emu.lunarcore.server.packet.CmdId;
import emu.lunarcore.server.packet.Opcodes;
import emu.lunarcore.server.packet.PacketHandler;

@Opcodes(CmdId.PromoteAvatarCsReq)
public class HandlerPromoteAvatarCsReq extends PacketHandler {

    @Override
    public void handle(GameSession session, byte[] data) throws Exception {
        var req = PromoteAvatarCsReq.parseFrom(data);

        session.getServer().getInventoryService().promoteAvatar(session.getPlayer(), req.getBaseAvatarId());
        session.send(CmdId.PromoteAvatarScRsp);
    }

}
