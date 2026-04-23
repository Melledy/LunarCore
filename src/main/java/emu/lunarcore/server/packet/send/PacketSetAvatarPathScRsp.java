package emu.lunarcore.server.packet.send;

import emu.lunarcore.proto.SetAvatarPathScRspOuterClass.SetAvatarPathScRsp;
import emu.lunarcore.server.packet.BasePacket;
import emu.lunarcore.server.packet.CmdId;

public class PacketSetAvatarPathScRsp extends BasePacket {

    public PacketSetAvatarPathScRsp(int pathId) {
        super(CmdId.SetAvatarPathScRsp);

        var data = SetAvatarPathScRsp.newInstance();

        if (pathId != 0) {
            data.setAvatarIdValue(pathId);
        } else {
            data.setRetcode(1);
        }

        this.setData(data);
    }
}
