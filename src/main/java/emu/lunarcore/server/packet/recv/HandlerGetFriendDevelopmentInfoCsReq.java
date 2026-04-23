package emu.lunarcore.server.packet.recv;

import emu.lunarcore.proto.GetFriendDevelopmentInfoCsReqOuterClass.GetFriendDevelopmentInfoCsReq;
import emu.lunarcore.server.game.GameSession;
import emu.lunarcore.server.packet.CmdId;
import emu.lunarcore.server.packet.Opcodes;
import emu.lunarcore.server.packet.PacketHandler;
import emu.lunarcore.server.packet.send.PacketGetFriendDevelopmentInfoScRsp;

@Opcodes(CmdId.GetFriendDevelopmentInfoCsReq)
public class HandlerGetFriendDevelopmentInfoCsReq extends PacketHandler {

    @Override
    public void handle(GameSession session, byte[] data) throws Exception {
        var req = GetFriendDevelopmentInfoCsReq.parseFrom(data);

        session.send(new PacketGetFriendDevelopmentInfoScRsp(req.getUid()));
    }

}
