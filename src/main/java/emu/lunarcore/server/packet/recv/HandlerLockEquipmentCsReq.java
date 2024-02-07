package emu.lunarcore.server.packet.recv;

import emu.lunarcore.proto.LockEquipmentCsReqOuterClass.LockEquipmentCsReq;
import emu.lunarcore.server.game.GameSession;
import emu.lunarcore.server.packet.CmdId;
import emu.lunarcore.server.packet.Opcodes;
import emu.lunarcore.server.packet.PacketHandler;

@Opcodes(CmdId.LockEquipmentCsReq)
public class HandlerLockEquipmentCsReq extends PacketHandler {

    @Override
    public void handle(GameSession session, byte[] data) throws Exception {
        var req = LockEquipmentCsReq.parseFrom(data);

        session.getServer().getInventoryService().lockItem(session.getPlayer(), req.getEquipmentUniqueId(), req.getIsProtected());
        session.send(CmdId.LockEquipmentScRsp);
    }

}
