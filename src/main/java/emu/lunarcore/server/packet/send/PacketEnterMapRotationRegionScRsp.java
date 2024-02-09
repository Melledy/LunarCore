package emu.lunarcore.server.packet.send;

import emu.lunarcore.proto.EnterMapRotationRegionScRspOuterClass.EnterMapRotationRegionScRsp;
import emu.lunarcore.server.packet.BasePacket;
import emu.lunarcore.server.packet.CmdId;
import emu.lunarcore.proto.MotionInfoOuterClass.MotionInfo;;

public class PacketEnterMapRotationRegionScRsp extends BasePacket {

    public PacketEnterMapRotationRegionScRsp(MotionInfo motionInfo) {
        super(CmdId.EnterMapRotationRegionScRsp);

        var data = EnterMapRotationRegionScRsp.newInstance()
            .setMotion(motionInfo);

        this.setData(data);
    }
}
