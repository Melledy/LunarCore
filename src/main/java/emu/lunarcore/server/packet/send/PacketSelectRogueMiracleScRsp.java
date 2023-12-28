package emu.lunarcore.server.packet.send;

import emu.lunarcore.game.rogue.RogueMiracleData;
import emu.lunarcore.game.rogue.RogueMiracleSelectMenu;
import emu.lunarcore.proto.SelectRogueMiracleScRspOuterClass.SelectRogueMiracleScRsp;
import emu.lunarcore.server.packet.BasePacket;
import emu.lunarcore.server.packet.CmdId;

public class PacketSelectRogueMiracleScRsp extends BasePacket {

    public PacketSelectRogueMiracleScRsp(RogueMiracleData miracle, RogueMiracleSelectMenu miracleSelect) {
        super(CmdId.NONE); // TODO update
        
        var data = SelectRogueMiracleScRsp.newInstance();
        
        if (miracleSelect != null) {
            data.setMiracleSelectInfo(miracleSelect.toProto());
        } else {
            data.getMutableMiracleSelectInfo();
        }
        
        this.setData(data);
    }
}
