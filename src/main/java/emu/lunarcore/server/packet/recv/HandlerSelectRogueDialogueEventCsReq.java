package emu.lunarcore.server.packet.recv;

import emu.lunarcore.game.scene.entity.EntityNpc;
import emu.lunarcore.proto.FinishRogueDialogueGroupCsReqOuterClass.FinishRogueDialogueGroupCsReq;
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
        
        if (session.getPlayer().getRogueInstance() != null) {
            session.getPlayer().getRogueInstance().onSelectDialogue(req.getDialogueEventId());
        }

        EntityNpc npc = (EntityNpc)session.getPlayer().getScene().getEntityById(req.getEntityId());
        
        session.send(new PacketSelectRogueDialogueEventScRsp(req.getDialogueEventId(), npc));
        new HandlerFinishRogueDialogueGroupCsReq().handle(session, FinishRogueDialogueGroupCsReq.newInstance()  // using it before the event is implemented
                .setEntityId(req.getEntityId())
                .toByteArray());
    }

}
