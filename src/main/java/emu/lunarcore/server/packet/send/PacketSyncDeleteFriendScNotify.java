package emu.lunarcore.server.packet.send;

import emu.lunarcore.proto.SyncDeleteFriendScNotifyOuterClass.SyncDeleteFriendScNotify;
import emu.lunarcore.server.packet.BasePacket;
import emu.lunarcore.server.packet.CmdId;

public class PacketSyncDeleteFriendScNotify extends BasePacket {

    public PacketSyncDeleteFriendScNotify(int uid) {
        super(CmdId.SyncDeleteFriendScNotify);
        
        var data = SyncDeleteFriendScNotify.newInstance()
                .setUid(uid);
        
        this.setData(data);
    }
}
