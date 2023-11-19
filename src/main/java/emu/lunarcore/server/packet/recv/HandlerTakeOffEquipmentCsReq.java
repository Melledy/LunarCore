package emu.lunarcore.server.packet.recv;

import emu.lunarcore.GameConstants;
import emu.lunarcore.proto.TakeOffEquipmentCsReqOuterClass.TakeOffEquipmentCsReq;
import emu.lunarcore.server.game.GameSession;
import emu.lunarcore.server.packet.CmdId;
import emu.lunarcore.server.packet.Opcodes;
import emu.lunarcore.server.packet.PacketHandler;

@Opcodes(CmdId.TakeOffEquipmentCsReq)
public class HandlerTakeOffEquipmentCsReq extends PacketHandler {

    @Override
    public void handle(GameSession session, byte[] data) throws Exception {
        var req = TakeOffEquipmentCsReq.parseFrom(data);

        session.getPlayer().getInventory().unequipItem(req.getBaseAvatarId(), GameConstants.EQUIPMENT_SLOT_ID);
        session.send(CmdId.TakeOffEquipmentScRsp);
    }

}
