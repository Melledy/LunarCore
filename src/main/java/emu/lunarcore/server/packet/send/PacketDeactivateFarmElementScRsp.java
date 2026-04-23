package emu.lunarcore.server.packet.send;

import emu.lunarcore.proto.DeactivateFarmElementScRspOuterClass.DeactivateFarmElementScRsp;
import emu.lunarcore.server.packet.BasePacket;
import emu.lunarcore.server.packet.CmdId;

public class PacketDeactivateFarmElementScRsp extends BasePacket {

    public PacketDeactivateFarmElementScRsp(int entityId) {
        super(CmdId.DeactivateFarmElementScRsp);

        var data = DeactivateFarmElementScRsp.newInstance()
                .setEntityId(entityId);


        this.setData(data);
    }
}
