package emu.lunarcore.server.packet.recv;

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
        
        session.send(new PacketSelectRogueDialogueEventScRsp(req.getDialogueEventId()));
    }

}
