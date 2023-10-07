package emu.lunarcore.server.packet.send;

import java.util.Collection;

import emu.lunarcore.game.chat.ChatMessage;
import emu.lunarcore.proto.GetPrivateChatHistoryScRspOuterClass.GetPrivateChatHistoryScRsp;
import emu.lunarcore.server.packet.BasePacket;
import emu.lunarcore.server.packet.CmdId;

public class PacketGetPrivateChatHistoryScRsp extends BasePacket {

    public PacketGetPrivateChatHistoryScRsp(int targetUid, Collection<ChatMessage> messages) {
        super(CmdId.GetPrivateChatHistoryScRsp);
        
        var data = GetPrivateChatHistoryScRsp.newInstance()
                .setToUid(targetUid);
        
        if (messages != null) {
            for (var message : messages) {
                data.addChatList(message.toProto());
            }
        }
        
        this.setData(data);
    }
}
