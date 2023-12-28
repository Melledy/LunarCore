package emu.lunarcore.server.packet.send;

import emu.lunarcore.game.rogue.RogueBuffSelectMenu;
import emu.lunarcore.proto.SyncRogueBuffSelectInfoScNotifyOuterClass.SyncRogueBuffSelectInfoScNotify;
import emu.lunarcore.server.packet.BasePacket;
import emu.lunarcore.server.packet.CmdId;

public class PacketSyncRogueBuffSelectInfoScNotify extends BasePacket {

    public PacketSyncRogueBuffSelectInfoScNotify(RogueBuffSelectMenu selectMenu) {
        super(CmdId.NONE); // TODO update
        
        var data = SyncRogueBuffSelectInfoScNotify.newInstance()
                .setBuffSelectInfo(selectMenu.toProto());
        
        this.setData(data);
    }
}
