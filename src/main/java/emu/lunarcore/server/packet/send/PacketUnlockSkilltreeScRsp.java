package emu.lunarcore.server.packet.send;

import emu.lunarcore.proto.UnlockSkilltreeScRspOuterClass.UnlockSkilltreeScRsp;
import emu.lunarcore.server.packet.BasePacket;
import emu.lunarcore.server.packet.CmdId;

public class PacketUnlockSkilltreeScRsp extends BasePacket {

    public PacketUnlockSkilltreeScRsp(int avatarId, int pointId, int level) {
        super(CmdId.UnlockSkilltreeScRsp);

        var data = UnlockSkilltreeScRsp.newInstance()
                .setBaseAvatarId(avatarId)
                .setPointId(pointId)
                .setLevel(level);

        this.setData(data);
    }

    public PacketUnlockSkilltreeScRsp() {
        super(CmdId.UnlockSkilltreeScRsp);

        var data = UnlockSkilltreeScRsp.newInstance().setRetcode(1);

        this.setData(data);
    }
}
