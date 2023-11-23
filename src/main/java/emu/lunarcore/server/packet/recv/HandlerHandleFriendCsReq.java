package emu.lunarcore.server.packet.recv;

import emu.lunarcore.proto.HandleFriendCsReqOuterClass.HandleFriendCsReq;
import emu.lunarcore.server.game.GameSession;
import emu.lunarcore.server.packet.CmdId;
import emu.lunarcore.server.packet.Opcodes;
import emu.lunarcore.server.packet.PacketHandler;

@Opcodes(CmdId.HandleFriendCsReq)
public class HandlerHandleFriendCsReq extends PacketHandler {

    @Override
    public void handle(GameSession session, byte[] data) throws Exception {
        var req = HandleFriendCsReq.parseFrom(data);
        
        session.getPlayer().getFriendList().handleFriendRequest(req.getUid(), req.getHandleResult());
    }

}
