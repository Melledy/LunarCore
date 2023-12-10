package emu.lunarcore.server.packet.recv;

import emu.lunarcore.proto.LockRelicCsReqOuterClass.LockRelicCsReq;
import emu.lunarcore.server.game.GameSession;
import emu.lunarcore.server.packet.CmdId;
import emu.lunarcore.server.packet.Opcodes;
import emu.lunarcore.server.packet.PacketHandler;

@Opcodes(CmdId.LockRelicCsReq)
public class HandlerLockRelicCsReq extends PacketHandler {

    @Override
    public void handle(GameSession session, byte[] data) throws Exception {
        var req = LockRelicCsReq.parseFrom(data);

        session.getServer().getInventoryService().lockEquip(session.getPlayer(), req.getRelicUniqueId(), req.getIsProtected());
        session.send(CmdId.LockRelicScRsp);
    }

}
