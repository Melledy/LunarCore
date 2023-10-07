package emu.lunarcore.server.packet.send;

import emu.lunarcore.game.scene.entity.EntityProp;
import emu.lunarcore.proto.InteractPropScRspOuterClass.InteractPropScRsp;
import emu.lunarcore.server.packet.BasePacket;
import emu.lunarcore.server.packet.CmdId;

public class PacketInteractPropScRsp extends BasePacket {

    public PacketInteractPropScRsp(EntityProp prop) {
        super(CmdId.InteractPropScRsp);
        
        var data = InteractPropScRsp.newInstance();
        
        if (prop != null) {
            data.setPropEntityId(prop.getEntityId());
            data.setPropState(prop.getState().getVal());
        }
        
        this.setData(data);
    }
}
