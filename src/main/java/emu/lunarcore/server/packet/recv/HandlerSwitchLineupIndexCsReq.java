package emu.lunarcore.server.packet.recv;

import emu.lunarcore.proto.SwitchLineupIndexCsReqOuterClass.SwitchLineupIndexCsReq;
import emu.lunarcore.server.game.GameSession;
import emu.lunarcore.server.packet.CmdId;
import emu.lunarcore.server.packet.Opcodes;
import emu.lunarcore.server.packet.PacketHandler;
import emu.lunarcore.server.packet.send.PacketSwitchLineupIndexScRsp;

@Opcodes(CmdId.SwitchLineupIndexCsReq)
public class HandlerSwitchLineupIndexCsReq extends PacketHandler {

    @Override
    public void handle(GameSession session, byte[] data) throws Exception {
        var req = SwitchLineupIndexCsReq.parseFrom(data);

        session.getPlayer().getLineupManager().switchLineup(req.getIndex());
        session.send(new PacketSwitchLineupIndexScRsp(session.getPlayer().getCurrentLineup()));
    }

}
