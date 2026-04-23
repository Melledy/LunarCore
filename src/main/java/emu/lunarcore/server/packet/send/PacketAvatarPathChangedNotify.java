package emu.lunarcore.server.packet.send;

import emu.lunarcore.game.avatar.AvatarMultiPath;
import emu.lunarcore.game.avatar.GameAvatar;
import emu.lunarcore.proto.AvatarPathChangedNotifyOuterClass.AvatarPathChangedNotify;
import emu.lunarcore.server.packet.BasePacket;
import emu.lunarcore.server.packet.CmdId;

public class PacketAvatarPathChangedNotify extends BasePacket {

    public PacketAvatarPathChangedNotify(GameAvatar avatar, AvatarMultiPath path) {
        super(CmdId.AvatarPathChangedNotify);

        var data = AvatarPathChangedNotify.newInstance()
                .setBaseAvatarId(avatar.getAvatarId())
                .setChangedAvatarTypeValue(path.getExcelId());

        this.setData(data);
    }
}
