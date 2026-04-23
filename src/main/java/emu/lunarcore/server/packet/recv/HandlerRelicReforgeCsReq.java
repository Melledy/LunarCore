package emu.lunarcore.server.packet.recv;

import emu.lunarcore.proto.RelicReforgeCsReqOuterClass.RelicReforgeCsReq;
import emu.lunarcore.server.game.GameSession;
import emu.lunarcore.server.packet.CmdId;
import emu.lunarcore.server.packet.Opcodes;
import emu.lunarcore.server.packet.PacketHandler;

@Opcodes(CmdId.RelicReforgeCsReq)
public class HandlerRelicReforgeCsReq extends PacketHandler {

    @Override
    public void handle(GameSession session, byte[] data) throws Exception {
        var req = RelicReforgeCsReq.parseFrom(data);

        session.getServer().getInventoryService().reforgeRelic(session.getPlayer(), req.getRelicUniqueId());
        session.send(CmdId.RelicReforgeScRsp);
    }

}
