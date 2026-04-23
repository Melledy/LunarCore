package emu.lunarcore.server.packet.send;

import emu.lunarcore.proto.ExchangeHcoinScRspOuterClass.ExchangeHcoinScRsp;
import emu.lunarcore.server.packet.BasePacket;
import emu.lunarcore.server.packet.CmdId;

public class PacketExchangeHcoinScRsp extends BasePacket {

    public PacketExchangeHcoinScRsp(int num) {
        super(CmdId.ExchangeHcoinScRsp);

        var data = ExchangeHcoinScRsp.newInstance().setNum(num);

        this.setData(data);
    }
}
