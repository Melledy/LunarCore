package emu.lunarcore.server.packet.recv;

import emu.lunarcore.proto.JoinLineupCsReqOuterClass.JoinLineupCsReq;
import emu.lunarcore.server.game.GameSession;
import emu.lunarcore.server.packet.CmdId;
import emu.lunarcore.server.packet.Opcodes;
import emu.lunarcore.server.packet.PacketHandler;

@Opcodes(CmdId.JoinLineupCsReq)
public class HandlerJoinLineupCsReq extends PacketHandler {

    @Override
    public void handle(GameSession session, byte[] data) throws Exception {
        var req = JoinLineupCsReq.parseFrom(data);

        session.getPlayer().getLineupManager().joinLineup(req.getIndex(), req.getSlot(), req.getBaseAvatarId());
        session.send(CmdId.JoinLineupScRsp);
    }

}
