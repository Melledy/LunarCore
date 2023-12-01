package emu.lunarcore.server.packet.send;

import emu.lunarcore.game.player.Player;
import emu.lunarcore.proto.SelectChatBubbleScRspOuterClass.SelectChatBubbleScRsp;
import emu.lunarcore.server.packet.BasePacket;
import emu.lunarcore.server.packet.CmdId;

public class PacketSelectChatBubbleScRsp extends BasePacket {

    public PacketSelectChatBubbleScRsp(Player player, int bubbleId) {
        super(CmdId.SelectChatBubbleScRsp);
        
        player.setChatBubble(bubbleId);

        var data = SelectChatBubbleScRsp.newInstance()
            .setCurChatBubble(bubbleId); 
        
        this.setData(data);
    }
}
