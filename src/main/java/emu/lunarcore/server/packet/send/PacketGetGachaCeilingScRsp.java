package emu.lunarcore.server.packet.send;

import emu.lunarcore.server.packet.CmdId;
import emu.lunarcore.server.game.GameSession;
import emu.lunarcore.server.packet.BasePacket;
import emu.lunarcore.proto.GachaCeilingOuterClass.GachaCeiling;
import emu.lunarcore.proto.GachaCeilingAvatarOuterClass.GachaCeilingAvatar;
import emu.lunarcore.proto.GetGachaCeilingScRspOuterClass.GetGachaCeilingScRsp;

public class PacketGetGachaCeilingScRsp extends BasePacket {

    private GachaCeilingAvatar createCeilingAvatarInfo(int avatarId) {
        return GachaCeilingAvatar.newInstance()
            .setRepeatedCnt(1)
            .setAvatarId(avatarId);
    }

    public PacketGetGachaCeilingScRsp(int unkfield) {
        super(CmdId.GetGachaCeilingScRsp);

        GachaCeilingAvatar ceilingavatarinfo1 = createCeilingAvatarInfo(1003);
        GachaCeilingAvatar ceilingavatarinfo2 = createCeilingAvatarInfo(1107);
        GachaCeilingAvatar ceilingavatarinfo3 = createCeilingAvatarInfo(1211);


        GachaCeiling gachaceiling = GachaCeiling.newInstance()
            .addAvatarList(ceilingavatarinfo1)
            .addAvatarList(ceilingavatarinfo2)
            .addAvatarList(ceilingavatarinfo3)
            .setCeilingNum(169);

        var proto = GetGachaCeilingScRsp.newInstance()
            .setGachaCeiling(gachaceiling)
            .setUnkfield(unkfield);
        this.setData(proto);
    }
}
