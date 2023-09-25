package emu.lunarcore.server.packet.send;

import emu.lunarcore.proto.SetLineupNameScRspOuterClass.SetLineupNameScRsp;
import emu.lunarcore.server.packet.BasePacket;
import emu.lunarcore.server.packet.CmdId;

public class PacketSetLineupNameScRsp extends BasePacket {

    public PacketSetLineupNameScRsp(String name) {
        super(CmdId.SetLineupNameScRsp);

        var data = SetLineupNameScRsp.newInstance();

        if (name != null) {
            data.setName(name);
        } else {
            data.setRetcode(1);
        }

        this.setData(data);
    }
}
