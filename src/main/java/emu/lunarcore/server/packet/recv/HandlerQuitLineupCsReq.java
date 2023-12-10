package emu.lunarcore.server.packet.recv;

import emu.lunarcore.proto.QuitLineupCsReqOuterClass.QuitLineupCsReq;
import emu.lunarcore.server.game.GameSession;
import emu.lunarcore.server.packet.CmdId;
import emu.lunarcore.server.packet.Opcodes;
import emu.lunarcore.server.packet.PacketHandler;

@Opcodes(CmdId.QuitLineupCsReq)
public class HandlerQuitLineupCsReq extends PacketHandler {

    @Override
    public void handle(GameSession session, byte[] data) throws Exception {
        var req = QuitLineupCsReq.parseFrom(data);

        session.getPlayer().getLineupManager().quitLineup(req.getIndex(), req.getBaseAvatarId());
        session.send(CmdId.QuitLineupScRsp);
    }

}
