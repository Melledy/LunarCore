package emu.lunarcore.server.packet.send;

import emu.lunarcore.proto.UnlockAvatarSkinScNotifyOuterClass.UnlockAvatarSkinScNotify;
import emu.lunarcore.server.packet.BasePacket;
import emu.lunarcore.server.packet.CmdId;

public class PacketUnlockAvatarSkinScNotify extends BasePacket {

    public PacketUnlockAvatarSkinScNotify(int skinId) {
        super(CmdId.UnlockAvatarSkinScNotify);

        var data = UnlockAvatarSkinScNotify.newInstance()
                .setAvatarSkinId(skinId);

        this.setData(data);
    }
}
