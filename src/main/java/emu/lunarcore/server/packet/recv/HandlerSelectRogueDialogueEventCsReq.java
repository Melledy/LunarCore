package emu.lunarcore.server.packet.recv;

import emu.lunarcore.game.scene.entity.EntityNpc;
import emu.lunarcore.proto.SelectRogueDialogueEventCsReqOuterClass.SelectRogueDialogueEventCsReq;
import emu.lunarcore.server.game.GameSession;
import emu.lunarcore.server.packet.CmdId;
import emu.lunarcore.server.packet.Opcodes;
import emu.lunarcore.server.packet.PacketHandler;
import emu.lunarcore.server.packet.send.PacketSelectRogueDialogueEventScRsp;

@Opcodes(CmdId.SelectRogueDialogueEventCsReq)
public class HandlerSelectRogueDialogueEventCsReq extends PacketHandler {

    @Override
    public void handle(GameSession session, byte[] data) throws Exception {
        var req = SelectRogueDialogueEventCsReq.parseFrom(data);

        EntityNpc npc = (EntityNpc)session.getPlayer().getScene().getEntityById(req.getEntityId());
        
        if (npc == null) return;
        
        int callback = 0;
        if (session.getPlayer().getRogueInstance() != null) {
            callback = session.getPlayer().getRogueInstance().onSelectDialogue(req.getDialogueEventId(), npc.getRogueNpcId());
        }
        
        session.send(new PacketSelectRogueDialogueEventScRsp(req.getDialogueEventId(), npc, callback));
    }

}
