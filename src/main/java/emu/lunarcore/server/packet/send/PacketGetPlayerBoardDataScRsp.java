package emu.lunarcore.server.packet.send;

import emu.lunarcore.game.player.Player;
import emu.lunarcore.proto.DisplayAvatarOuterClass.DisplayAvatar;
import emu.lunarcore.proto.GetPlayerBoardDataScRspOuterClass.GetPlayerBoardDataScRsp;
import emu.lunarcore.proto.HeadIconOuterClass.HeadIcon;
import emu.lunarcore.server.packet.BasePacket;
import emu.lunarcore.server.packet.CmdId;

public class PacketGetPlayerBoardDataScRsp extends BasePacket {

    public PacketGetPlayerBoardDataScRsp(Player player) {
        super(CmdId.GetPlayerBoardDataScRsp);

        var data = GetPlayerBoardDataScRsp.newInstance()
                .setCurrentHeadIconId(player.getHeadIcon())
                .setSignature(player.getSignature());

        // Create display avatar object
        data.getMutableDisplayAvatarVec();

        // Head icons, aka profile pictures
        for (int id : player.getUnlocks().getHeadIcons()) {
            data.addUnlockedHeadIconList(HeadIcon.newInstance().setId(id));
        }

        // Assist avatars
        for (var objectId : player.getAssistAvatars()) {
            var avatar = player.getAvatarById(objectId);
            if (avatar == null) continue;

            data.addDisplaySupportAvatarVec(avatar.getAvatarId());
        }

        // Display avatars
        for (int i = 0; i < player.getDisplayAvatars().size(); i++) {
            var objectId = player.getDisplayAvatars().get(i);
            if (objectId == null) continue;

            var avatar = player.getAvatarById(objectId);
            if (avatar == null) continue;

            var info = DisplayAvatar.newInstance()
                    .setAvatarId(avatar.getAvatarId())
                    .setPos(i);

            data.getMutableDisplayAvatarVec().addDisplayAvatarList(info);
        }

        this.setData(data);
    }
}
