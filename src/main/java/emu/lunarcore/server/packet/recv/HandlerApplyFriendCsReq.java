package emu.lunarcore.server.packet.recv;

import emu.lunarcore.proto.ApplyFriendCsReqOuterClass.ApplyFriendCsReq;
import emu.lunarcore.server.game.GameSession;
import emu.lunarcore.server.packet.CmdId;
import emu.lunarcore.server.packet.Opcodes;
import emu.lunarcore.server.packet.PacketHandler;

@Opcodes(CmdId.ApplyFriendCsReq)
public class HandlerApplyFriendCsReq extends PacketHandler {

    @Override
    public void handle(GameSession session, byte[] data) throws Exception {
        var req = ApplyFriendCsReq.parseFrom(data);
        
        session.getPlayer().getFriendList().sendFriendRequest(req.getUid());
        session.send(CmdId.ApplyFriendScRsp);
    }

}
