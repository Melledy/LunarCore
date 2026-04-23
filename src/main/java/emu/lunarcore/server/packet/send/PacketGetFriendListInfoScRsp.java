package emu.lunarcore.server.packet.send;

import emu.lunarcore.GameConstants;
import emu.lunarcore.LunarCore;
import emu.lunarcore.game.challenge.ChallengeInstance;
import emu.lunarcore.game.friends.FriendList;
import emu.lunarcore.proto.AssistSimpleInfoOuterClass.AssistSimpleInfo;
import emu.lunarcore.proto.FriendListInfoOuterClass.FriendListInfo;
import emu.lunarcore.proto.FriendOnlineStatusOuterClass.FriendOnlineStatus;
import emu.lunarcore.proto.GetFriendListInfoScRspOuterClass.GetFriendListInfoScRsp;
import emu.lunarcore.proto.PlatformTypeOuterClass.PlatformType;
import emu.lunarcore.proto.PlayerSimpleInfoOuterClass.PlayerSimpleInfo;
import emu.lunarcore.proto.PlayingStateOuterClass.PlayingState;
import emu.lunarcore.server.packet.BasePacket;
import emu.lunarcore.server.packet.CmdId;

public class PacketGetFriendListInfoScRsp extends BasePacket {

    public PacketGetFriendListInfoScRsp(FriendList friendList) {
        super(CmdId.GetFriendListInfoScRsp);

        // Get friend info from config
        var serverFriendInfo = LunarCore.getConfig().getServerOptions().getServerFriendInfo();

        // Inject server console as friend
        var consoleFriend = PlayerSimpleInfo.newInstance()
                .setUid(GameConstants.SERVER_CONSOLE_UID)
                .setNickname(serverFriendInfo.getName())
                .setSignature(serverFriendInfo.getSignature())
                .setLevel(serverFriendInfo.getLevel())
                .setChatBubble(serverFriendInfo.getChatBubbleId())
                .setOnlineStatus(FriendOnlineStatus.FRIEND_ONLINE_STATUS_ONLINE)
                .setPlatform(PlatformType.PC)
                .setHeadIcon(serverFriendInfo.getHeadIcon());

        // Add server display avatars
        if (serverFriendInfo.getDisplayAvatars() != null) {
            for (int pos = 0; pos < serverFriendInfo.getDisplayAvatars().size(); pos++) {
                var displayAvatar = serverFriendInfo.getDisplayAvatars().get(pos);

                var info = AssistSimpleInfo.newInstance()
                        .setAvatarId(displayAvatar.getAvatarId())
                        .setLevel(displayAvatar.getLevel())
                        .setPos(pos);

                consoleFriend.addAssistInfoList(info);
            }
        }

        // Create proto and add the server console info to it
        var data = GetFriendListInfoScRsp.newInstance()
                .addFriendList(FriendListInfo.newInstance().setSimpleInfo(consoleFriend));

        // Add friend infos
        for (var friendship : friendList.getFriends().values()) {
            // Get player
            var friend = friendList.getServer().getPlayerByUid(friendship.getFriendUid(), true);
            if (friend == null) continue;

            // Create friend info
            var friendInfo = FriendListInfo.newInstance()
                    .setSimpleInfo(friend.toSimpleInfo());

            // Set playing state
            if (friend.isOnline()) {
                // Check if friend is in a challenge
                ChallengeInstance challengeInstance = friend.getInstance(ChallengeInstance.class);
                if (challengeInstance != null) {
                    PlayingState state = switch (challengeInstance.getType()) {
                        case STORY -> PlayingState.PLAYING_CHALLENGE_STORY;
                        case BOSS -> PlayingState.PLAYING_CHALLENGE_STORY;
                        default -> PlayingState.PLAYING_CHALLENGE_MEMORY;
                    };
                    friendInfo.setPlayingState(state);
                }
            }

            data.addFriendList(friendInfo);
        }

        this.setData(data);
    }
}
