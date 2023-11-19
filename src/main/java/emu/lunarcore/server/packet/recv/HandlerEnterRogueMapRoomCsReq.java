package emu.lunarcore.server.packet.recv;

import emu.lunarcore.game.rogue.RogueRoomData;
import emu.lunarcore.proto.EnterRogueMapRoomCsReqOuterClass.EnterRogueMapRoomCsReq;
import emu.lunarcore.server.game.GameSession;
import emu.lunarcore.server.packet.CmdId;
import emu.lunarcore.server.packet.Opcodes;
import emu.lunarcore.server.packet.PacketHandler;
import emu.lunarcore.server.packet.send.PacketEnterRogueMapRoomScRsp;

@Opcodes(CmdId.EnterRogueMapRoomCsReq)
public class HandlerEnterRogueMapRoomCsReq extends PacketHandler {

    @Override
    public void handle(GameSession session, byte[] data) throws Exception {
        var req = EnterRogueMapRoomCsReq.parseFrom(data);
        
        RogueRoomData enteredRoom = null;
        if (session.getPlayer().getRogueInstance() != null) {
            enteredRoom = session.getPlayer().getRogueInstance().enterRoom(req.getSiteId());
        }
        
        if (enteredRoom != null) {
            session.send(new PacketEnterRogueMapRoomScRsp(session.getPlayer(), enteredRoom));
        } else {
            session.send(CmdId.EnterRogueMapRoomScRsp);
        }
    }

}
