package emu.lunarcore.server.packet.send;

import emu.lunarcore.proto.GetNpcTakenRewardScRspOuterClass.GetNpcTakenRewardScRsp;
import emu.lunarcore.server.packet.BasePacket;
import emu.lunarcore.server.packet.CmdId;

public class PacketGetNpcTakenRewardScRsp extends BasePacket {

    public PacketGetNpcTakenRewardScRsp(int npcId) {
        super(CmdId.GetNpcTakenRewardScRsp);
        
        var data = GetNpcTakenRewardScRsp.newInstance()
                .setNpcId(npcId);
        
        this.setData(data);
    }
}
