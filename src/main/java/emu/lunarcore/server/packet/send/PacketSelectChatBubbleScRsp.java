package emu.lunarcore.server.packet.send;

import emu.lunarcore.proto.SelectChatBubbleScRspOuterClass.SelectChatBubbleScRsp;
import emu.lunarcore.server.packet.BasePacket;
import emu.lunarcore.server.packet.CmdId;

public class PacketSelectChatBubbleScRsp extends BasePacket {
    
    public PacketSelectChatBubbleScRsp() {
        super(CmdId.SelectChatBubbleScRsp);

        var data = SelectChatBubbleScRsp.newInstance()
            .setRetcode(1);
        
        this.setData(data);
    }

    public PacketSelectChatBubbleScRsp(int bubbleId) {
        super(CmdId.SelectChatBubbleScRsp);

        var data = SelectChatBubbleScRsp.newInstance()
            .setCurChatBubble(bubbleId); 
        
        this.setData(data);
    }
}
