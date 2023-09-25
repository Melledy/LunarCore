package emu.lunarcore.server.packet.recv;

import emu.lunarcore.proto.UnlockSkilltreeCsReqOuterClass.UnlockSkilltreeCsReq;
import emu.lunarcore.server.game.GameSession;
import emu.lunarcore.server.packet.CmdId;
import emu.lunarcore.server.packet.Opcodes;
import emu.lunarcore.server.packet.PacketHandler;

@Opcodes(CmdId.UnlockSkilltreeCsReq)
public class HandlerUnlockSkilltreeCsReq extends PacketHandler {

    @Override
    public void handle(GameSession session, byte[] header, byte[] data) throws Exception {
        var req = UnlockSkilltreeCsReq.parseFrom(data);

        session.getServer().getInventoryService().unlockSkillTreeAvatar(session.getPlayer(), req.getPointId());
    }

}
