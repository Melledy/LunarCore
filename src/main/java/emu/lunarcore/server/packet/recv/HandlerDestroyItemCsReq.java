package emu.lunarcore.server.packet.recv;

import emu.lunarcore.proto.DestroyItemCsReqOuterClass.DestroyItemCsReq;
import emu.lunarcore.server.game.GameSession;
import emu.lunarcore.server.packet.CmdId;
import emu.lunarcore.server.packet.Opcodes;
import emu.lunarcore.server.packet.PacketHandler;

@Opcodes(CmdId.DestroyItemCsReq)
public class HandlerDestroyItemCsReq extends PacketHandler {

    @Override
    public void handle(GameSession session, byte[] data) throws Exception {
        var req = DestroyItemCsReq.parseFrom(data);

        session.getServer().getInventoryService().destroyItem(session.getPlayer(), req.getItemId(), req.getItemCount());
        session.send(CmdId.DestroyItemScRsp);
    }

}
