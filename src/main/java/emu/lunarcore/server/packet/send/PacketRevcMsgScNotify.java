package emu.lunarcore.server.packet.send;

import emu.lunarcore.game.chat.ChatMessage;
import emu.lunarcore.proto.ChatTypeOuterClass.ChatType;
import emu.lunarcore.proto.RevcMsgScNotifyOuterClass.RevcMsgScNotify;
import emu.lunarcore.server.packet.BasePacket;
import emu.lunarcore.server.packet.CmdId;

public class PacketRevcMsgScNotify extends BasePacket {

    public PacketRevcMsgScNotify(ChatMessage message) {
        super(CmdId.RevcMsgScNotify);

        var data = RevcMsgScNotify.newInstance()
                .setToUid(message.getToUid())
                .setChatType(ChatType.CHAT_TYPE_PRIVATE)
                .setMsg(message.toProto());

        this.setData(data);
    }
}
