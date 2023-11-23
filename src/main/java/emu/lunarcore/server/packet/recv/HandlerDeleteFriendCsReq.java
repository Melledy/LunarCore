package emu.lunarcore.server.packet.recv;

import emu.lunarcore.proto.DeleteFriendCsReqOuterClass.DeleteFriendCsReq;
import emu.lunarcore.server.game.GameSession;
import emu.lunarcore.server.packet.CmdId;
import emu.lunarcore.server.packet.Opcodes;
import emu.lunarcore.server.packet.PacketHandler;

@Opcodes(CmdId.DeleteFriendCsReq)
public class HandlerDeleteFriendCsReq extends PacketHandler {

    @Override
    public void handle(GameSession session, byte[] data) throws Exception {
        var req = DeleteFriendCsReq.parseFrom(data);
        
        session.getPlayer().getFriendList().deleteFriend(req.getUid());
        session.send(CmdId.DeleteFriendScRsp);
    }

}
