package emu.lunarcore.server.packet.send;

import emu.lunarcore.proto.MultiPathAvatarTypeOuterClass.MultiPathAvatarType;
import emu.lunarcore.proto.UnlockAvatarPathScRspOuterClass.UnlockAvatarPathScRsp;
import emu.lunarcore.server.packet.BasePacket;
import emu.lunarcore.server.packet.CmdId;

public class PacketUnlockAvatarPathScRsp extends BasePacket {

    public PacketUnlockAvatarPathScRsp(MultiPathAvatarType avatarId) {
        super(CmdId.UnlockAvatarPathScRsp);

        var data = UnlockAvatarPathScRsp.newInstance()
                .setAvatarId(avatarId);

        this.setData(data);
    }
}
