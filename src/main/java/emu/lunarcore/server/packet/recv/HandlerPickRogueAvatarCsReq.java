package emu.lunarcore.server.packet.recv;

import emu.lunarcore.proto.PickRogueAvatarCsReqOuterClass.PickRogueAvatarCsReq;
import emu.lunarcore.server.game.GameSession;
import emu.lunarcore.server.packet.CmdId;
import emu.lunarcore.server.packet.Opcodes;
import emu.lunarcore.server.packet.PacketHandler;

@Opcodes(CmdId.PickRogueAvatarCsReq)
public class HandlerPickRogueAvatarCsReq extends PacketHandler {
    @Override
    public void handle(GameSession session, byte[] data) throws Exception {
        var proto = PickRogueAvatarCsReq.parseFrom(data);
        
        session.getPlayer().getRogueInstance().pickAvatar(proto.getBaseAvatarList());
    }
}
