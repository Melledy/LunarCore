package emu.lunarcore.server.packet.recv;

import emu.lunarcore.proto.TakeOffRelicCsReqOuterClass.TakeOffRelicCsReq;
import emu.lunarcore.server.game.GameSession;
import emu.lunarcore.server.packet.CmdId;
import emu.lunarcore.server.packet.Opcodes;
import emu.lunarcore.server.packet.PacketHandler;

@Opcodes(CmdId.TakeOffRelicCsReq)
public class HandlerTakeOffRelicCsReq extends PacketHandler {

    @Override
    public void handle(GameSession session, byte[] data) throws Exception {
        var req = TakeOffRelicCsReq.parseFrom(data);

        for (int slot : req.getSlotList()) {
            session.getPlayer().getInventory().unequipItem(req.getBaseAvatarId(), slot);
        }

        session.send(CmdId.TakeOffRelicScRsp);
    }

}
