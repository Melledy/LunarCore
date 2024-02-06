package emu.lunarcore.server.packet.send;

import emu.lunarcore.GameConstants;
import emu.lunarcore.LunarCore;
import emu.lunarcore.game.friends.FriendList;
import emu.lunarcore.proto.AssistSimpleInfoOuterClass.AssistSimpleInfo;
import emu.lunarcore.proto.FriendListInfoOuterClass.FriendListInfo;
import emu.lunarcore.proto.FriendOnlineStatusOuterClass.FriendOnlineStatus;
import emu.lunarcore.proto.GetFriendListInfoScRspOuterClass.GetFriendListInfoScRsp;
import emu.lunarcore.proto.PlatformTypeOuterClass.PlatformType;
import emu.lunarcore.proto.SimpleInfoOuterClass.SimpleInfo;
import emu.lunarcore.server.packet.BasePacket;
import emu.lunarcore.server.packet.CmdId;

public class PacketGetFriendListInfoScRsp extends BasePacket {

    public PacketGetFriendListInfoScRsp(FriendList friendList) {
        super(CmdId.GetFriendListInfoScRsp);
        
        // Get friend info from config
        var serverFriendInfo = LunarCore.getConfig().getServerOptions().getServerFriendInfo();

        // Inject server console as friend
        var consoleFriend = SimpleInfo.newInstance()
                .setUid(GameConstants.SERVER_CONSOLE_UID)
                .setNickname(serverFriendInfo.getName())
                .setSignature(serverFriendInfo.getSignature())
                .setLevel(serverFriendInfo.getLevel())
                .setChatBubbleId(serverFriendInfo.getChatBubbleId())
                .setOnlineStatus(FriendOnlineStatus.FRIEND_ONLINE_STATUS_ONLINE)
                .setPlatformType(PlatformType.PC)
                .addAssistSimpleInfo(AssistSimpleInfo.newInstance().setAvatarId(serverFriendInfo.getDisplayAvatarId()).setLevel(serverFriendInfo.getDisplayAvatarLevel()))
                .setHeadIcon(serverFriendInfo.getHeadIcon());

        var data = GetFriendListInfoScRsp.newInstance()
                .addFriendList(FriendListInfo.newInstance().setSimpleInfo(consoleFriend));
        
        for (var friendship : friendList.getFriends().values()) {
            var friend = friendList.getServer().getPlayerByUid(friendship.getFriendUid(), true);
            if (friend == null) continue;
            
            var friendInfo = FriendListInfo.newInstance()
                    .setSimpleInfo(friend.toSimpleInfo());
            
            data.addFriendList(friendInfo);
        }

        this.setData(data);
    }
}
