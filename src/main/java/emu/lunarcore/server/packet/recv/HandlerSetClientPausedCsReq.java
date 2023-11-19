package emu.lunarcore.server.packet.recv;

import emu.lunarcore.proto.SetClientPausedCsReqOuterClass.SetClientPausedCsReq;
import emu.lunarcore.server.game.GameSession;
import emu.lunarcore.server.packet.CmdId;
import emu.lunarcore.server.packet.Opcodes;
import emu.lunarcore.server.packet.PacketHandler;
import emu.lunarcore.server.packet.send.PacketSetClientPausedScRsp;

@Opcodes(CmdId.SetClientPausedCsReq)
public class HandlerSetClientPausedCsReq extends PacketHandler {

    @Override
    public void handle(GameSession session, byte[] data) throws Exception {
        var req = SetClientPausedCsReq.parseFrom(data);
        
        session.getPlayer().setPaused(req.getPaused());
        session.send(new PacketSetClientPausedScRsp(session.getPlayer()));
    }

}
