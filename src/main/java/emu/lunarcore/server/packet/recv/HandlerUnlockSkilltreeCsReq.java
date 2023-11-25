package emu.lunarcore.server.packet.recv;

import emu.lunarcore.proto.UnlockSkilltreeCsReqOuterClass.UnlockSkilltreeCsReq;
import emu.lunarcore.server.game.GameSession;
import emu.lunarcore.server.packet.CmdId;
import emu.lunarcore.server.packet.Opcodes;
import emu.lunarcore.server.packet.PacketHandler;
import emu.lunarcore.server.packet.send.PacketUnlockSkilltreeScRsp;

@Opcodes(CmdId.UnlockSkilltreeCsReq)
public class HandlerUnlockSkilltreeCsReq extends PacketHandler {

    @Override
    public void handle(GameSession session, byte[] data) throws Exception {
        var req = UnlockSkilltreeCsReq.parseFrom(data);
        int avatarId = req.getPointId() / 1000; // Hacky way to get avatar id

        boolean success = session.getServer().getInventoryService().unlockSkillTreeAvatar(session.getPlayer(), avatarId, req.getPointId());
        
        if (success) {
            session.send(new PacketUnlockSkilltreeScRsp(avatarId, req.getPointId(), req.getLevel()));
        } else {
            session.send(new PacketUnlockSkilltreeScRsp()); 
        }
    }

}
