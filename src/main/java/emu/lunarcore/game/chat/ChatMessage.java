package emu.lunarcore.game.chat;

import emu.lunarcore.LunarCore;
import emu.lunarcore.proto.ChatContentInfoOuterClass.ChatContentInfo;
import emu.lunarcore.proto.ChatInfoOuterClass.ChatInfo;
import emu.lunarcore.proto.MsgTypeOuterClass.MsgType;
import lombok.Getter;

@Getter
public class ChatMessage {
    private int fromUid;
    private int toUid;
    private String text;
    private int emote;
    private long time;
    
    public ChatMessage(int fromUid, int toUid) {
        this.fromUid = fromUid;
        this.toUid = toUid;
        this.time = System.currentTimeMillis();
    }
    
    public ChatMessage(int fromUid, int toUid, String text) {
        this(fromUid, toUid);
        this.text = text;
    }
    
    public ChatMessage(int fromUid, int toUid, int emote) {
        this(fromUid, toUid);
        this.emote = emote;
    }

    public MsgType getType() {
        return this.getText() != null ? MsgType.MSG_TYPE_CUSTOM_TEXT : MsgType.MSG_TYPE_EMOJI;
    }

    public ChatInfo toProto() {
        var proto = ChatInfo.newInstance()
                .setSentTime(LunarCore.convertToServerTime(this.getTime()) / 1000);
        
        proto.getMutableChatHeader()
                .setUid(this.getFromUid());
        
        var content = ChatContentInfo.newInstance();
        
        if (this.getText() != null) {
            content.setMsgType(MsgType.MSG_TYPE_CUSTOM_TEXT);
            content.getMutableChatMsg().setChatText(this.getText());
        } else {
            content.setMsgType(MsgType.MSG_TYPE_EMOJI);
            content.getMutableChatMsg().setEmoteId(this.getEmote());
        }
        
        proto.addChatContentList(content);
        
        return proto;
    }
}
