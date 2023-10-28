package emu.lunarcore.server.packet.send;

import emu.lunarcore.game.player.Player;
import emu.lunarcore.proto.SyncRogueVirtualItemInfoScNotifyOuterClass.SyncRogueVirtualItemInfoScNotify;
import emu.lunarcore.server.packet.BasePacket;
import emu.lunarcore.server.packet.CmdId;

public class PacketSyncRogueVirtualItemInfoScNotify extends BasePacket {

    public PacketSyncRogueVirtualItemInfoScNotify(Player player) {
        super(CmdId.SyncRogueVirtualItemInfoScNotify);
        
        var data = SyncRogueVirtualItemInfoScNotify.newInstance()
                .setRogueVirtualItemInfo(player.toRogueVirtualItemsProto());
        
        this.setData(data);
    }
}
