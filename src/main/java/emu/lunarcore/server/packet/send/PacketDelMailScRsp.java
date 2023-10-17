package emu.lunarcore.server.packet.send;

import emu.lunarcore.proto.DelMailScRspOuterClass.DelMailScRsp;
import emu.lunarcore.server.packet.BasePacket;
import emu.lunarcore.server.packet.CmdId;
import it.unimi.dsi.fastutil.ints.IntList;

public class PacketDelMailScRsp extends BasePacket {

    public PacketDelMailScRsp(IntList deleteList) {
        super(CmdId.DelMailScRsp);
        
        var data = DelMailScRsp.newInstance();
        
        deleteList.forEach(data::addIdList);
        
        this.setData(data);
    }
}
