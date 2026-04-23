package emu.lunarcore.server.packet.recv;

import emu.lunarcore.proto.RelicReforgeConfirmCsReqOuterClass.RelicReforgeConfirmCsReq;
import emu.lunarcore.server.game.GameSession;
import emu.lunarcore.server.packet.CmdId;
import emu.lunarcore.server.packet.Opcodes;
import emu.lunarcore.server.packet.PacketHandler;

@Opcodes(CmdId.RelicReforgeConfirmCsReq)
public class HandlerRelicReforgeConfirmCsReq extends PacketHandler {

    @Override
    public void handle(GameSession session, byte[] data) throws Exception {
        var req = RelicReforgeConfirmCsReq.parseFrom(data);

        session.getServer().getInventoryService().confirmReforgeRelic(session.getPlayer(), req.getRelicUniqueId(), req.getKeepOldAffixes());
        session.send(CmdId.RelicReforgeConfirmScRsp);
    }

}
