package emu.lunarcore.server.packet.send;

import emu.lunarcore.proto.EnhanceRogueBuffScRspOuterClass.EnhanceRogueBuffScRsp;
import emu.lunarcore.proto.RogueBuffOuterClass.RogueBuff;
import emu.lunarcore.server.packet.BasePacket;
import emu.lunarcore.server.packet.CmdId;

public class PacketEnhanceRogueBuffScRsp extends BasePacket {
    public PacketEnhanceRogueBuffScRsp(RogueBuff buff) {
        super(CmdId.EnhanceRogueBuffScRsp);
        
        var proto = EnhanceRogueBuffScRsp.newInstance()
            .setIsSuccess(true)
            .setRogueBuff(buff);
        
        this.setData(proto);
    }
}
