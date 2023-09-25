package emu.lunarcore.server.packet.send;

import emu.lunarcore.proto.GetPlayerDetailInfoScRspOuterClass.GetPlayerDetailInfoScRsp;
import emu.lunarcore.server.packet.BasePacket;
import emu.lunarcore.server.packet.CmdId;

public class PacketGetPlayerDetailInfoScRsp extends BasePacket {

    public PacketGetPlayerDetailInfoScRsp() {
        super(CmdId.GetPlayerDetailInfoScRsp);

        // TODO handle properly
        var data = GetPlayerDetailInfoScRsp.newInstance()
                .setRetcode(1);

        this.setData(data);
    }
}
