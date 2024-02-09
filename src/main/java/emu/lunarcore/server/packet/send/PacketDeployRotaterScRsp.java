package emu.lunarcore.server.packet.send;

import emu.lunarcore.proto.DeployRotaterScRspOuterClass.DeployRotaterScRsp;
import emu.lunarcore.proto.RotaterDataOuterClass.RotaterData;
import emu.lunarcore.proto.RotatorEnergyInfoOuterClass.RotatorEnergyInfo;
import emu.lunarcore.server.packet.BasePacket;
import emu.lunarcore.server.packet.CmdId;

public class PacketDeployRotaterScRsp extends BasePacket {

    public PacketDeployRotaterScRsp(RotaterData rotaterData) {
        super(CmdId.DeployRotaterScRsp);

        var energyInfo = RotatorEnergyInfo.newInstance()
            .setMaxNum(100)
            .setCurNum(100);

        var data = DeployRotaterScRsp.newInstance()
            .setRotaterData(rotaterData)
            .setEnergyInfo(energyInfo);

        this.setData(data);
    }
}
