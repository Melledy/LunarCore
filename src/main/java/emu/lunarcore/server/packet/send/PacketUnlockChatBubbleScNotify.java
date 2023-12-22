package emu.lunarcore.server.packet.send;

import emu.lunarcore.proto.UnlockChatBubbleScNotifyOuterClass.UnlockChatBubbleScNotify;
import emu.lunarcore.server.packet.BasePacket;
import emu.lunarcore.server.packet.CmdId;

public class PacketUnlockChatBubbleScNotify extends BasePacket {

    public PacketUnlockChatBubbleScNotify(int id) {
        super(CmdId.UnlockChatBubbleScNotify);
        
        var data = UnlockChatBubbleScNotify.newInstance()
                .setBubbleId(id);
        
        this.setData(data);
    }
}
