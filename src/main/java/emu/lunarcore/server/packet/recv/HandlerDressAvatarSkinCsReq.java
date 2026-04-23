package emu.lunarcore.server.packet.recv;

import emu.lunarcore.proto.DressAvatarSkinCsReqOuterClass.DressAvatarSkinCsReq;
import emu.lunarcore.server.game.GameSession;
import emu.lunarcore.server.packet.CmdId;
import emu.lunarcore.server.packet.Opcodes;
import emu.lunarcore.server.packet.PacketHandler;

@Opcodes(CmdId.DressAvatarSkinCsReq)
public class HandlerDressAvatarSkinCsReq extends PacketHandler {

    @Override
    public void handle(GameSession session, byte[] data) throws Exception {
        var req = DressAvatarSkinCsReq.parseFrom(data);
        
        // Verify that we have the skin
        if (!session.getPlayer().getUnlocks().getAvatarSkins().contains(req.getAvatarSkinId())) {
            session.send(CmdId.DressAvatarSkinScRsp);
            return;
        }

        // Get avatar
        var avatar = session.getPlayer().getAvatarById(req.getAvatarId());
        if (avatar == null) {
            session.send(CmdId.DressAvatarSkinScRsp);
            return;
        }
        
        // Set skin
        avatar.setSkin(req.getAvatarSkinId());
        session.send(CmdId.DressAvatarSkinScRsp);
    }

}
