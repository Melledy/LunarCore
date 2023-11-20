package emu.lunarcore.server.packet.send;

import emu.lunarcore.game.avatar.AvatarHeroPath;
import emu.lunarcore.game.player.Player;
import emu.lunarcore.proto.GetHeroBasicTypeInfoScRspOuterClass.GetHeroBasicTypeInfoScRsp;
import emu.lunarcore.server.packet.BasePacket;
import emu.lunarcore.server.packet.CmdId;

public class PacketGetHeroBasicTypeInfoScRsp extends BasePacket {

    public PacketGetHeroBasicTypeInfoScRsp(Player player) {
        super(CmdId.GetHeroBasicTypeInfoScRsp);

        var data = GetHeroBasicTypeInfoScRsp.newInstance()
                .setGenderValue(player.getGender().getVal())
                .setCurBasicTypeValue(player.getCurBasicType());
        
        for (AvatarHeroPath path : player.getAvatars().getHeroPaths().values()) {
            data.addAllBasicTypeInfoList(path.toProto());
        }

        this.setData(data);
    }
}
