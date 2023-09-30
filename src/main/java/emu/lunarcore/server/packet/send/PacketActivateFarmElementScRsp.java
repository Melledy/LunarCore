package emu.lunarcore.server.packet.send;

import emu.lunarcore.proto.ActivateFarmElementScRspOuterClass.ActivateFarmElementScRsp;
import emu.lunarcore.server.packet.BasePacket;
import emu.lunarcore.server.packet.CmdId;

public class PacketActivateFarmElementScRsp extends BasePacket {

    public PacketActivateFarmElementScRsp() {
        super(CmdId.ActivateFarmElementScRsp);
        
        var data = ActivateFarmElementScRsp.newInstance()
                .setRetcode(1);
        
        this.setData(data);
    }
    
    public PacketActivateFarmElementScRsp(int entityId, int worldLevel) {
        super(CmdId.ActivateFarmElementScRsp);
        
        var data = ActivateFarmElementScRsp.newInstance()
                .setEntityId(entityId)
                .setWorldLevel(worldLevel);
        
        this.setData(data);
    }
}
