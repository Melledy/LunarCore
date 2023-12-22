package emu.lunarcore.server.packet.recv;

import emu.lunarcore.proto.SetHeadIconCsReqOuterClass.SetHeadIconCsReq;
import emu.lunarcore.server.game.GameSession;
import emu.lunarcore.server.packet.CmdId;
import emu.lunarcore.server.packet.Opcodes;
import emu.lunarcore.server.packet.PacketHandler;
import emu.lunarcore.server.packet.send.PacketSetHeadIconScRsp;

@Opcodes(CmdId.SetHeadIconCsReq)
public class HandlerSetHeadIconCsReq extends PacketHandler {

    @Override
    public void handle(GameSession session, byte[] data) throws Exception {
        var req = SetHeadIconCsReq.parseFrom(data);
        
        if (session.getPlayer().setHeadIcon(req.getId())) {
            // Success
            session.send(new PacketSetHeadIconScRsp(req.getId()));
        } else {
            // Failure (player didnt have the head icon)
            session.send(new PacketSetHeadIconScRsp());
        }
    }

}
