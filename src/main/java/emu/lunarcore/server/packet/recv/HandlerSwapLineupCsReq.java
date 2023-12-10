package emu.lunarcore.server.packet.recv;

import emu.lunarcore.proto.SwapLineupCsReqOuterClass.SwapLineupCsReq;
import emu.lunarcore.server.game.GameSession;
import emu.lunarcore.server.packet.CmdId;
import emu.lunarcore.server.packet.Opcodes;
import emu.lunarcore.server.packet.PacketHandler;

@Opcodes(CmdId.SwapLineupCsReq)
public class HandlerSwapLineupCsReq extends PacketHandler {

    @Override
    public void handle(GameSession session, byte[] data) throws Exception {
        var req = SwapLineupCsReq.parseFrom(data);

        session.getPlayer().getLineupManager().swapLineup(req.getIndex(), req.getSrcSlot(), req.getDstSlot());
        session.send(CmdId.SwapLineupScRsp);
    }

}
