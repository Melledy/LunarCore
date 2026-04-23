package emu.lunarcore.server.packet.recv;

import emu.lunarcore.data.GameData;
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
        // Parse request
        var req = UnlockSkilltreeCsReq.parseFrom(data);
        
        // Get excel for avatar id
        var excel = GameData.getAvatarSkillTreeExcelMap().get(req.getPointId(), req.getLevel());

        if (excel == null) {
            session.send(new PacketUnlockSkilltreeScRsp());
            return;
        }
        
        // Unlock skill tree
        int avatarId = excel.getAvatarID();
        boolean success = session.getServer().getInventoryService().unlockSkillTreeAvatar(session.getPlayer(), avatarId, excel.getAnchorId(), req.getLevel());

        if (success) {
            session.send(new PacketUnlockSkilltreeScRsp(avatarId, req.getPointId(), req.getLevel()));
        } else {
            session.send(new PacketUnlockSkilltreeScRsp());
        }
    }

}
