package emu.lunarcore.server.packet.send;

import emu.lunarcore.game.player.Player;
import emu.lunarcore.proto.SetAssistAvatarScRspOuterClass.SetAssistAvatarScRsp;
import emu.lunarcore.server.packet.BasePacket;
import emu.lunarcore.server.packet.CmdId;

public class PacketSetAssistAvatarScRsp extends BasePacket {

    public PacketSetAssistAvatarScRsp(Player player) {
        super(CmdId.SetAssistAvatarScRsp);

        var data = SetAssistAvatarScRsp.newInstance();

        for (var objectId : player.getAssistAvatars()) {
            var avatar = player.getAvatarById(objectId);
            if (avatar == null) continue;

            data.addAvatarIdList(avatar.getAvatarId());
        }

        this.setData(data);
    }
}
