package emu.lunarcore.server.packet.send;

import emu.lunarcore.game.chat.ChatMessage;
import emu.lunarcore.proto.ChatTypeOuterClass.ChatType;
import emu.lunarcore.proto.MsgTypeOuterClass.MsgType;
import emu.lunarcore.proto.RevcMsgScNotifyOuterClass.RevcMsgScNotify;
import emu.lunarcore.server.packet.BasePacket;
import emu.lunarcore.server.packet.CmdId;

public class PacketRevcMsgScNotify extends BasePacket {

    public PacketRevcMsgScNotify(ChatMessage message) {
        super(CmdId.RevcMsgScNotify);

        var data = RevcMsgScNotify.newInstance()
                .setChatType(ChatType.CHAT_TYPE_PRIVATE)
                .setFromUid(message.getFromUid())
                .setToUid(message.getToUid());
        
        MsgType msgType = message.getType();
        data.setMsgType(msgType);
        
        if (msgType == MsgType.MSG_TYPE_CUSTOM_TEXT) {
            data.setText(message.getText());
        } else {
            data.setEmote(message.getEmote());
        }

        this.setData(data);
    }
}
