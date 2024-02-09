package emu.lunarcore.server.packet.send;

import emu.lunarcore.proto.MotionInfoOuterClass.MotionInfo;
import emu.lunarcore.proto.RotateMapScRspOuterClass.RotateMapScRsp;
import emu.lunarcore.server.packet.BasePacket;
import emu.lunarcore.server.packet.CmdId;

public class PacketRotateMapScRsp extends BasePacket {

    public PacketRotateMapScRsp(MotionInfo motion) {
        super(CmdId.RotateMapScRsp);
        
        var data = RotateMapScRsp.newInstance()
            .setMotion(motion);
    
        this.setData(data);
    }
}
