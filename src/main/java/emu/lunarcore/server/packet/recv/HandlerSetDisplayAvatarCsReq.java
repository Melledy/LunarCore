package emu.lunarcore.server.packet.recv;

import emu.lunarcore.proto.SetDisplayAvatarCsReqOuterClass.SetDisplayAvatarCsReq;
import emu.lunarcore.server.game.GameSession;
import emu.lunarcore.server.packet.CmdId;
import emu.lunarcore.server.packet.Opcodes;
import emu.lunarcore.server.packet.PacketHandler;
import emu.lunarcore.server.packet.send.PacketSetDisplayAvatarScRsp;

@Opcodes(CmdId.SetDisplayAvatarCsReq)
public class HandlerSetDisplayAvatarCsReq extends PacketHandler {

    @Override
    public void handle(GameSession session, byte[] data) throws Exception {
        var req = SetDisplayAvatarCsReq.parseFrom(data);

        session.getPlayer().setDisplayAvatars(req.getDisplayAvatarList());
        session.send(new PacketSetDisplayAvatarScRsp(session.getPlayer()));
    }

}
