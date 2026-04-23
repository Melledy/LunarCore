package emu.lunarcore.server.packet.send;

import emu.lunarcore.game.avatar.GameAvatar;
import emu.lunarcore.proto.MarkAvatarScRspOuterClass.MarkAvatarScRsp;
import emu.lunarcore.server.packet.BasePacket;
import emu.lunarcore.server.packet.CmdId;

public class PacketMarkAvatarScRsp extends BasePacket {

    public PacketMarkAvatarScRsp(GameAvatar avatar) {
        super(CmdId.MarkAvatarScRsp);

        var data = MarkAvatarScRsp.newInstance();

        if (avatar != null) {
            data.setAvatarId(avatar.getAvatarId())
            .setIsMarked(avatar.isMarked());
        } else {
            data.setRetcode(1);
        }

        this.setData(data);
    }
}
