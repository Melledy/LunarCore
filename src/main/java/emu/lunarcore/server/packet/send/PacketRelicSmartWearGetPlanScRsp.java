package emu.lunarcore.server.packet.send;

import emu.lunarcore.proto.RelicSmartWearGetPlanScRspOuterClass.RelicSmartWearGetPlanScRsp;
import emu.lunarcore.server.packet.BasePacket;
import emu.lunarcore.server.packet.CmdId;

public class PacketRelicSmartWearGetPlanScRsp extends BasePacket {

    public PacketRelicSmartWearGetPlanScRsp(int avatarId) {
        super(CmdId.RelicSmartWearGetPlanScRsp);

        var data = RelicSmartWearGetPlanScRsp.newInstance();

        data.setAvatarId(avatarId);

        this.setData(data);
    }
}
