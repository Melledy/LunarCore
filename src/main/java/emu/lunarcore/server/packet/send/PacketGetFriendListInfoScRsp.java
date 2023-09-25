package emu.lunarcore.server.packet.send;

import emu.lunarcore.GameConstants;
import emu.lunarcore.proto.FriendAvatarInfoOuterClass.FriendAvatarInfo;
import emu.lunarcore.proto.FriendListInfoOuterClass.FriendListInfo;
import emu.lunarcore.proto.FriendOnlineStatusOuterClass.FriendOnlineStatus;
import emu.lunarcore.proto.GetFriendListInfoScRspOuterClass.GetFriendListInfoScRsp;
import emu.lunarcore.proto.PlatformTypeOuterClass.PlatformType;
import emu.lunarcore.proto.SimpleInfoOuterClass.SimpleInfo;
import emu.lunarcore.server.packet.BasePacket;
import emu.lunarcore.server.packet.CmdId;

public class PacketGetFriendListInfoScRsp extends BasePacket {

    public PacketGetFriendListInfoScRsp() {
        super(CmdId.GetFriendListInfoScRsp);

        var consoleFriend = SimpleInfo.newInstance()
                .setNickname("Server")
                .setLevel(1)
                .setUid(GameConstants.SERVER_CONSOLE_UID)
                .setOnlineStatus(FriendOnlineStatus.FRIEND_ONLINE_STATUS_ONLINE)
                .setPlatformType(PlatformType.PC)
                .setFriendAvatarInfo(FriendAvatarInfo.newInstance().setAvatarId(1001).setLevel(1))
                .setProfilePicture(201001);

        var data = GetFriendListInfoScRsp.newInstance()
                .addFriendList(FriendListInfo.newInstance().setSimpleInfo(consoleFriend));

        this.setData(data);
    }
}
