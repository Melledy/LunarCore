package emu.lunarcore.server.packet.recv;

import emu.lunarcore.proto.PromoteEquipmentCsReqOuterClass.PromoteEquipmentCsReq;
import emu.lunarcore.server.game.GameSession;
import emu.lunarcore.server.packet.CmdId;
import emu.lunarcore.server.packet.Opcodes;
import emu.lunarcore.server.packet.PacketHandler;

@Opcodes(CmdId.PromoteEquipmentCsReq)
public class HandlerPromoteEquipmentCsReq extends PacketHandler {

    @Override
    public void handle(GameSession session, byte[] data) throws Exception {
        var req = PromoteEquipmentCsReq.parseFrom(data);

        session.getServer().getInventoryService().promoteEquipment(session.getPlayer(), req.getEquipmentUniqueId());
        session.send(CmdId.PromoteEquipmentScRsp);
    }

}
