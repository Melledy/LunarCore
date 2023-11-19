package emu.lunarcore.server.packet.recv;

import emu.lunarcore.proto.GetPrivateChatHistoryCsReqOuterClass.GetPrivateChatHistoryCsReq;
import emu.lunarcore.server.game.GameSession;
import emu.lunarcore.server.packet.CmdId;
import emu.lunarcore.server.packet.Opcodes;
import emu.lunarcore.server.packet.PacketHandler;
import emu.lunarcore.server.packet.send.PacketGetPrivateChatHistoryScRsp;

@Opcodes(CmdId.GetPrivateChatHistoryCsReq)
public class HandlerGetPrivateChatHistoryCsReq extends PacketHandler {

    @Override
    public void handle(GameSession session, byte[] data) throws Exception {
        var req = GetPrivateChatHistoryCsReq.parseFrom(data);
        
        var messages = session.getPlayer().getChatManager().getHistoryByUid(req.getToUid());
        
        session.send(new PacketGetPrivateChatHistoryScRsp(req.getToUid(), messages));
    }

}
