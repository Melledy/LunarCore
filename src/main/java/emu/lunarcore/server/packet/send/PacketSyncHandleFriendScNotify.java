package emu.lunarcore.server.packet.send;

import emu.lunarcore.game.player.Player;
import emu.lunarcore.proto.FriendListInfoOuterClass.FriendListInfo;
import emu.lunarcore.proto.SyncHandleFriendScNotifyOuterClass.SyncHandleFriendScNotify;
import emu.lunarcore.server.packet.BasePacket;
import emu.lunarcore.server.packet.CmdId;

public class PacketSyncHandleFriendScNotify extends BasePacket {

    public PacketSyncHandleFriendScNotify(Player friend, boolean result) {
        super(CmdId.SyncHandleFriendScNotify);
        
        var data = SyncHandleFriendScNotify.newInstance()
                .setUid(friend.getUid())
                .setHandleResult(result);
        
        if (result) {
            data.setHandleFriendInfo(FriendListInfo.newInstance().setSimpleInfo(friend.toSimpleInfo()));
        }
        
        this.setData(data);
    }
}
