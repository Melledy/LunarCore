package emu.lunarcore.server.packet.recv;

import emu.lunarcore.server.game.GameSession;
import emu.lunarcore.server.packet.CmdId;
import emu.lunarcore.server.packet.Opcodes;
import emu.lunarcore.server.packet.PacketHandler;
import emu.lunarcore.server.packet.send.PacketGetEnteredSceneScRsp;

@Opcodes(CmdId.GetEnteredSceneCsReq)
public class HandlerGetEnteredSceneCsReq extends PacketHandler {

    @Override
    public void handle(GameSession session, byte[] header, byte[] data) throws Exception {
        // Note: This packet is not necessary for the client to load into a scene
        session.send(new PacketGetEnteredSceneScRsp(session.getPlayer()));
    }

}
