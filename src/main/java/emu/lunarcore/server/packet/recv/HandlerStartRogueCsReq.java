package emu.lunarcore.server.packet.recv;

import emu.lunarcore.proto.StartRogueCsReqOuterClass.StartRogueCsReq;
import emu.lunarcore.server.game.GameSession;
import emu.lunarcore.server.packet.CmdId;
import emu.lunarcore.server.packet.Opcodes;
import emu.lunarcore.server.packet.PacketHandler;

@Opcodes(CmdId.StartRogueCsReq)
public class HandlerStartRogueCsReq extends PacketHandler {

    @Override
    public void handle(GameSession session, byte[] data) throws Exception {
        var req = StartRogueCsReq.parseFrom(data);
        
        session.getPlayer().getRogueManager().startRogue(req.getAreaId(), req.getBuffAeonId(), req.getBaseAvatarIdList());
    }

}
