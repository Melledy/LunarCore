package emu.lunarcore.game.chat;

import emu.lunarcore.LunarCore;
import emu.lunarcore.proto.ChatOuterClass.Chat;
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

    public Chat toProto() {
        var proto = Chat.newInstance()
                .setSenderUid(this.getFromUid())
                .setSentTime(LunarCore.convertToServerTime(this.getTime()) / 1000)
                .setMsgType(this.getType())
                .setEmote(this.getEmote());
        
        if (this.getText() != null) {
            proto.setText(text);
        }
        
        return proto;
    }
}
