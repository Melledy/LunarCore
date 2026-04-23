package emu.lunarcore.server.packet.recv;

import emu.lunarcore.proto.TakeOffAvatarSkinCsReqOuterClass.TakeOffAvatarSkinCsReq;
import emu.lunarcore.server.game.GameSession;
import emu.lunarcore.server.packet.CmdId;
import emu.lunarcore.server.packet.Opcodes;
import emu.lunarcore.server.packet.PacketHandler;

@Opcodes(CmdId.TakeOffAvatarSkinCsReq)
public class HandlerTakeOffAvatarSkinCsReq extends PacketHandler {

    @Override
    public void handle(GameSession session, byte[] data) throws Exception {
        var req = TakeOffAvatarSkinCsReq.parseFrom(data);
        
        // Get avatar
        var avatar = session.getPlayer().getAvatarById(req.getAvatarId());
        if (avatar == null) {
            session.send(CmdId.TakeOffAvatarSkinScRsp);
            return;
        }
        
        // Clear skin
        avatar.setSkin(0);
        session.send(CmdId.TakeOffAvatarSkinScRsp);
    }

}
