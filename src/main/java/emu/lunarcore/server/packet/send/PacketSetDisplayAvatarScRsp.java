package emu.lunarcore.server.packet.send;

import emu.lunarcore.game.player.Player;
import emu.lunarcore.proto.DisplayAvatarOuterClass.DisplayAvatar;
import emu.lunarcore.proto.SetDisplayAvatarScRspOuterClass.SetDisplayAvatarScRsp;
import emu.lunarcore.server.packet.BasePacket;
import emu.lunarcore.server.packet.CmdId;

public class PacketSetDisplayAvatarScRsp extends BasePacket {

    public PacketSetDisplayAvatarScRsp(Player player) {
        super(CmdId.SetDisplayAvatarScRsp);

        var data = SetDisplayAvatarScRsp.newInstance();

        for (int i = 0; i < player.getDisplayAvatars().size(); i++) {
            var objectId = player.getDisplayAvatars().get(i);
            if (objectId == null) continue;

            var avatar = player.getAvatarById(objectId);
            if (avatar == null) continue;

            var info = DisplayAvatar.newInstance()
                    .setAvatarId(avatar.getAvatarId())
                    .setPos(i);

            data.addDisplayAvatarList(info);
        }

        this.setData(data);
    }
}
