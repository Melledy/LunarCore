package emu.lunarcore.server.packet.send;

import emu.lunarcore.proto.SummonPetScRspOuterClass.SummonPetScRsp;
import emu.lunarcore.server.packet.BasePacket;
import emu.lunarcore.server.packet.CmdId;

public class PacketSummonPetScRsp extends BasePacket {
    
    public PacketSummonPetScRsp() {
        super(CmdId.SummonPetScRsp);
        
        var data = SummonPetScRsp.newInstance()
                .setRetcode(1);
        
        this.setData(data);
    }

    public PacketSummonPetScRsp(int petId) {
        super(CmdId.SummonPetScRsp);
        
        var data = SummonPetScRsp.newInstance()
                .setCurPetId(petId);
        
        this.setData(data);
    }
}
