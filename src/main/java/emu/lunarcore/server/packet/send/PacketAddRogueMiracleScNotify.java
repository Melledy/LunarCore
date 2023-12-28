package emu.lunarcore.server.packet.send;

import emu.lunarcore.game.rogue.RogueMiracleData;
import emu.lunarcore.proto.AddRogueMiracleScNotifyOuterClass.AddRogueMiracleScNotify;
import emu.lunarcore.proto.RogueMiracleSourceOuterClass.RogueMiracleSource;
import emu.lunarcore.server.packet.BasePacket;
import emu.lunarcore.server.packet.CmdId;

public class PacketAddRogueMiracleScNotify extends BasePacket {

    public PacketAddRogueMiracleScNotify(RogueMiracleData miracle, RogueMiracleSource rogueMiracleSource) {
        super(CmdId.NONE); // TODO update
        
        var data = AddRogueMiracleScNotify.newInstance()
                .setRogueMiracle(miracle.toProto())
                .setSource(rogueMiracleSource);
        
        this.setData(data);
    }
}
