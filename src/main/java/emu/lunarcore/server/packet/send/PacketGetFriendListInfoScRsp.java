package emu.lunarcore.server.packet.send;

import emu.lunarcore.GameConstants;
import emu.lunarcore.game.friends.FriendList;
import emu.lunarcore.proto.FriendListInfoOuterClass.FriendListInfo;
import emu.lunarcore.proto.FriendOnlineStatusOuterClass.FriendOnlineStatus;
import emu.lunarcore.proto.GetFriendListInfoScRspOuterClass.GetFriendListInfoScRsp;
import emu.lunarcore.proto.PlatformTypeOuterClass.PlatformType;
import emu.lunarcore.proto.SimpleAvatarInfoOuterClass.SimpleAvatarInfo;
import emu.lunarcore.proto.SimpleInfoOuterClass.SimpleInfo;
import emu.lunarcore.server.packet.BasePacket;
import emu.lunarcore.server.packet.CmdId;

public class PacketGetFriendListInfoScRsp extends BasePacket {

    public PacketGetFriendListInfoScRsp(FriendList friendList) {
        super(CmdId.GetFriendListInfoScRsp);

        // Inject server console as friend
        var consoleFriend = SimpleInfo.newInstance()
                .setUid(GameConstants.SERVER_CONSOLE_UID)
                .setNickname("Server")
                .setSignature("Type /help for a list of commands")
                .setLevel(1)
                .setOnlineStatus(FriendOnlineStatus.FRIEND_ONLINE_STATUS_ONLINE)
                .setPlatformType(PlatformType.PC)
                .setSimpleAvatarInfo(SimpleAvatarInfo.newInstance().setAvatarId(1001).setLevel(1))
                .setHeadIcon(201001);

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
