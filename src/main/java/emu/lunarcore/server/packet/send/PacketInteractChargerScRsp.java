package emu.lunarcore.server.packet.send;

import emu.lunarcore.proto.ChargerInfoOuterClass.ChargerInfo;
import emu.lunarcore.proto.InteractChargerScRspOuterClass.InteractChargerScRsp;
import emu.lunarcore.server.packet.BasePacket;
import emu.lunarcore.server.packet.CmdId;

public class PacketInteractChargerScRsp extends BasePacket {

    public PacketInteractChargerScRsp(ChargerInfo chargerInfo) {
        super(CmdId.InteractChargerScRsp);
        
        var data = InteractChargerScRsp.newInstance()
            .setChargerInfo(chargerInfo);
        
        this.setData(data);
    }
}
