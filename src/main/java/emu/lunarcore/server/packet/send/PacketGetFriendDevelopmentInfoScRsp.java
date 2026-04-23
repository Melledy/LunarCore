package emu.lunarcore.server.packet.send;

import emu.lunarcore.proto.DevelopmentTypeOuterClass.DevelopmentType;
import emu.lunarcore.proto.FriendDevelopmentInfoOuterClass.FriendDevelopmentInfo;
import emu.lunarcore.proto.GetFriendDevelopmentInfoScRspOuterClass.GetFriendDevelopmentInfoScRsp;
import emu.lunarcore.server.packet.BasePacket;
import emu.lunarcore.server.packet.CmdId;

public class PacketGetFriendDevelopmentInfoScRsp extends BasePacket {

    public PacketGetFriendDevelopmentInfoScRsp(int uid) {
        super(CmdId.GetFriendDevelopmentInfoScRsp);

        var data = GetFriendDevelopmentInfoScRsp.newInstance()
                .setUid(uid);

        // TODO. Dont show if not a friend
        data.addDevelopmentInfoList(createAvatarDevelopment(1308));
        data.addDevelopmentInfoList(createAvatarDevelopment(1309));
        data.addDevelopmentInfoList(createAvatarDevelopment(1315));
        data.addDevelopmentInfoList(createChallengeDevelopment(3212));

        this.setData(data);
    }

    private FriendDevelopmentInfo createAvatarDevelopment(int avatarId) {
        return FriendDevelopmentInfo.newInstance()
                .setDevelopmentType(DevelopmentType.DEVELOPMENT_UNLOCK_AVATAR)
                .setAvatarId(avatarId)
                .setTime(System.currentTimeMillis());
    }

    private FriendDevelopmentInfo createChallengeDevelopment(int challengeId) {
        var proto = FriendDevelopmentInfo.newInstance()
                .setDevelopmentType(DevelopmentType.DEVELOPMENT_MEMORY_CHALLENGE)
                .setTime(System.currentTimeMillis());

        proto.getMutableChallengeDevelopmentInfo().setChallengeId(challengeId);

        return proto;
    }
}
