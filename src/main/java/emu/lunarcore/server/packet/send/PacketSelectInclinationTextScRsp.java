package emu.lunarcore.server.packet.send;

import emu.lunarcore.proto.SelectInclinationTextScRspOuterClass.SelectInclinationTextScRsp;
import emu.lunarcore.server.packet.BasePacket;
import emu.lunarcore.server.packet.CmdId;

public class PacketSelectInclinationTextScRsp extends BasePacket {

    public PacketSelectInclinationTextScRsp(int id) {
        super(CmdId.SelectInclinationTextScRsp);

        var data = SelectInclinationTextScRsp.newInstance()
                .setInclinationTextId(id);

        this.setData(data);
    }
}