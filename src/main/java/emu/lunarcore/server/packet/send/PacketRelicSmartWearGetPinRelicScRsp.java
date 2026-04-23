package emu.lunarcore.server.packet.send;

import emu.lunarcore.proto.RelicSmartWearGetPinRelicScRspOuterClass.RelicSmartWearGetPinRelicScRsp;
import emu.lunarcore.server.packet.BasePacket;
import emu.lunarcore.server.packet.CmdId;

public class PacketRelicSmartWearGetPinRelicScRsp extends BasePacket {

    public PacketRelicSmartWearGetPinRelicScRsp(int avatarId) {
        super(CmdId.RelicSmartWearGetPinRelicScRsp);

        var data = RelicSmartWearGetPinRelicScRsp.newInstance()
                .setAvatarId(avatarId);


        this.setData(data);
    }
}
