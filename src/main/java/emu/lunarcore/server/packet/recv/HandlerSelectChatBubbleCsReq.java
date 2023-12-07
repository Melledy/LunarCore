package emu.lunarcore.server.packet.recv;

import emu.lunarcore.server.game.GameSession;
import emu.lunarcore.game.player.Player;
import emu.lunarcore.server.packet.CmdId;
import emu.lunarcore.server.packet.Opcodes;
import emu.lunarcore.server.packet.PacketHandler;
import emu.lunarcore.proto.SelectChatBubbleCsReqOuterClass.SelectChatBubbleCsReq;
import emu.lunarcore.server.packet.send.PacketSelectChatBubbleScRsp;

@Opcodes(CmdId.SelectChatBubbleCsReq)
public class HandlerSelectChatBubbleCsReq extends PacketHandler {

    @Override
    public void handle(GameSession session, byte[] data) throws Exception {

        var req = SelectChatBubbleCsReq.parseFrom(data);
        Player player = session.getPlayer();
        
        session.send(new PacketSelectChatBubbleScRsp(player, req.getBubbleId()));
    }
    
}
