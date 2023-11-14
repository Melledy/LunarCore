package emu.lunarcore.server.packet.send;

import emu.lunarcore.proto.ReserveStaminaExchangeScRspOuterClass.ReserveStaminaExchangeScRsp;
import emu.lunarcore.server.packet.BasePacket;
import emu.lunarcore.server.packet.CmdId;

public class PacketReserveStaminaExchangeScRsp extends BasePacket {

    public PacketReserveStaminaExchangeScRsp(int amount) {
        super(CmdId.ReserveStaminaExchangeScRsp);
        
        var data = ReserveStaminaExchangeScRsp.newInstance();
        
        if (amount > 0) {
            data.setNum(amount);
        } else {
            data.setRetcode(1);
        }
        
        this.setData(data);
    }
}
