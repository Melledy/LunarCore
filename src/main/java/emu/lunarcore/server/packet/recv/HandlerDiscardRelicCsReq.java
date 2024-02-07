package emu.lunarcore.server.packet.recv;

import emu.lunarcore.proto.DiscardRelicCsReqOuterClass.DiscardRelicCsReq;
import emu.lunarcore.server.game.GameSession;
import emu.lunarcore.server.packet.CmdId;
import emu.lunarcore.server.packet.Opcodes;
import emu.lunarcore.server.packet.PacketHandler;

@Opcodes(CmdId.DiscardRelicCsReq)
public class HandlerDiscardRelicCsReq extends PacketHandler {

    @Override
    public void handle(GameSession session, byte[] data) throws Exception {
        var req = DiscardRelicCsReq.parseFrom(data);
        
        session.getServer().getInventoryService().discardRelic(session.getPlayer(), req.getRelicUniqueId(), req.getIsDiscard());
        session.send(CmdId.DiscardRelicScRsp);
    }

}
