package emu.lunarcore.server.packet.send;

import emu.lunarcore.game.avatar.GameAvatar;
import emu.lunarcore.game.player.Player;
import emu.lunarcore.proto.GetAvatarDataScRspOuterClass.GetAvatarDataScRsp;
import emu.lunarcore.server.packet.BasePacket;
import emu.lunarcore.server.packet.CmdId;

public class PacketGetAvatarDataScRsp extends BasePacket {
    
    public PacketGetAvatarDataScRsp(Player player) {
        super(CmdId.GetAvatarDataScRsp);

        var data = GetAvatarDataScRsp.newInstance()
                .setIsGetAll(true);

        for (GameAvatar avatar : player.getAvatars()) {
            data.addAvatarList(avatar.toProto());
        }

        this.setData(data);
    }
    
}