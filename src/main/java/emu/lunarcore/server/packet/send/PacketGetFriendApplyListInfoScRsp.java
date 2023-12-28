package emu.lunarcore.server.packet.send;

import emu.lunarcore.game.friends.FriendList;
import emu.lunarcore.proto.FriendApplyInfoOuterClass.FriendApplyInfo;
import emu.lunarcore.proto.GetFriendApplyListInfoScRspOuterClass.GetFriendApplyListInfoScRsp;
import emu.lunarcore.server.packet.BasePacket;
import emu.lunarcore.server.packet.CmdId;

public class PacketGetFriendApplyListInfoScRsp extends BasePacket {

    public PacketGetFriendApplyListInfoScRsp(FriendList friendList) {
        super(CmdId.GetFriendApplyListInfoScRsp);

        var data = GetFriendApplyListInfoScRsp.newInstance();
        
        for (var friendship : friendList.getPendingFriends().values()) {
            // Skip if we are the asker
            if (friendship.getAskerUid() == friendList.getPlayer().getUid()) continue;
            
            // Get friend info from the server
            var friend = friendList.getServer().getPlayerByUid(friendship.getFriendUid(), true);
            if (friend == null) continue;
            
            var friendInfo = FriendApplyInfo.newInstance()
                    .setSimpleInfo(friend.toSimpleInfo());
            
            data.addFriendApplyList(friendInfo);
        }
        
        this.setData(data);
    }
}
