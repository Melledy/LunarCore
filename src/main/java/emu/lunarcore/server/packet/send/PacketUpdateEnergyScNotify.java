package emu.lunarcore.server.packet.send;

import emu.lunarcore.proto.RotatorEnergyInfoOuterClass.RotatorEnergyInfo;
import emu.lunarcore.proto.UpdateEnergyScNotifyOuterClass.UpdateEnergyScNotify;
import emu.lunarcore.server.packet.BasePacket;
import emu.lunarcore.server.packet.CmdId;

public class PacketUpdateEnergyScNotify extends BasePacket {

    public PacketUpdateEnergyScNotify() {
        super(CmdId.UpdateEnergyScNotify);

        var temp = RotatorEnergyInfo.newInstance()
            .setMaxNum(100)
            .setCurNum(100);

        var data = UpdateEnergyScNotify.newInstance()
            .setEnergyInfo(temp);

        this.setData(data);
    }
}
