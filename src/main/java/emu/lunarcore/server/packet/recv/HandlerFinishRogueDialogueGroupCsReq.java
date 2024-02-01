package emu.lunarcore.server.packet.recv;

import emu.lunarcore.game.scene.entity.EntityNpc;
import emu.lunarcore.proto.FinishRogueDialogueGroupCsReqOuterClass.FinishRogueDialogueGroupCsReq;
import emu.lunarcore.server.game.GameSession;
import emu.lunarcore.server.packet.CmdId;
import emu.lunarcore.server.packet.Opcodes;
import emu.lunarcore.server.packet.PacketHandler;
import emu.lunarcore.server.packet.send.PacketSceneGroupRefreshScNotify;

@Opcodes(CmdId.FinishRogueDialogueGroupCsReq)
public class HandlerFinishRogueDialogueGroupCsReq extends PacketHandler {
    @Override
    public void handle(GameSession session, byte[] data) throws Exception {
        var req = FinishRogueDialogueGroupCsReq.parseFrom(data);

        EntityNpc npc = (EntityNpc)session.getPlayer().getScene().getEntityById(req.getEntityId());
        if (npc == null) return;
        npc.setDialogueFinished(true);
        session.send(new PacketSceneGroupRefreshScNotify(npc, null));
        
        session.send(CmdId.FinishRogueDialogueGroupScRsp);
    }
}
