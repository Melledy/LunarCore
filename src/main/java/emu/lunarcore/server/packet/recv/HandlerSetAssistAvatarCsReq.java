package emu.lunarcore.server.packet.recv;

import emu.lunarcore.proto.SetAssistAvatarCsReqOuterClass.SetAssistAvatarCsReq;
import emu.lunarcore.server.game.GameSession;
import emu.lunarcore.server.packet.CmdId;
import emu.lunarcore.server.packet.Opcodes;
import emu.lunarcore.server.packet.PacketHandler;
import emu.lunarcore.server.packet.send.PacketSetAssistAvatarScRsp;

@Opcodes(CmdId.SetAssistAvatarCsReq)
public class HandlerSetAssistAvatarCsReq extends PacketHandler {

    @Override
    public void handle(GameSession session, byte[] data) throws Exception {
        var req = SetAssistAvatarCsReq.parseFrom(data);

        session.getPlayer().setAssistAvatars(req.getAvatarIdList());
        session.send(new PacketSetAssistAvatarScRsp(session.getPlayer()));
    }

}
