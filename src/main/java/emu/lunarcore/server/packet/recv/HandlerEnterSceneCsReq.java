package emu.lunarcore.server.packet.recv;

import emu.lunarcore.proto.EnterSceneCsReqOuterClass.EnterSceneCsReq;
import emu.lunarcore.server.game.GameSession;
import emu.lunarcore.server.packet.CmdId;
import emu.lunarcore.server.packet.Opcodes;
import emu.lunarcore.server.packet.PacketHandler;

@Opcodes(CmdId.EnterSceneCsReq)
public class HandlerEnterSceneCsReq extends PacketHandler {

    @Override
    public void handle(GameSession session, byte[] data) throws Exception {
        var req = EnterSceneCsReq.parseFrom(data);
        
        session.getPlayer().enterScene(req.getEntryId(), req.getTeleportId(), true);
        session.send(CmdId.EnterSceneScRsp);
    }

}
