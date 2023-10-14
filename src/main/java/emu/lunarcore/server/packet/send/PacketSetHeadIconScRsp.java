package emu.lunarcore.server.packet.send;

import emu.lunarcore.proto.SetHeadIconScRspOuterClass.SetHeadIconScRsp;
import emu.lunarcore.server.packet.BasePacket;
import emu.lunarcore.server.packet.CmdId;

public class PacketSetHeadIconScRsp extends BasePacket {

    public PacketSetHeadIconScRsp() {
        super(CmdId.SetHeadIconScRsp);
        
        var data = SetHeadIconScRsp.newInstance()
                .setRetcode(1);
        
        this.setData(data);
    }
    
    public PacketSetHeadIconScRsp(int headIconId) {
        super(CmdId.SetHeadIconScRsp);
        
        var data = SetHeadIconScRsp.newInstance()
                .setCurrentHeadIconId(headIconId);
        
        this.setData(data);
    }
}
