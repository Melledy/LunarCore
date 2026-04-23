package emu.lunarcore.server.packet.send;

import emu.lunarcore.proto.SetTrainWorldIdScRspOuterClass.SetTrainWorldIdScRsp;
import emu.lunarcore.server.packet.BasePacket;
import emu.lunarcore.server.packet.CmdId;

public class PacketSetTrainWorldIdScRsp extends BasePacket {

    public PacketSetTrainWorldIdScRsp(int trainWorldId) {
        super(CmdId.SetTrainWorldIdScRsp);

        var data = SetTrainWorldIdScRsp.newInstance()
                .setTrainWorldId(trainWorldId);
        
        this.setData(data);
    }
}
