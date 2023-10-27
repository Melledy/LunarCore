package emu.lunarcore.server.packet.send;

import emu.lunarcore.game.rogue.RogueMiracleSelectMenu;
import emu.lunarcore.proto.SyncRogueMiracleSelectInfoScNotifyOuterClass.SyncRogueMiracleSelectInfoScNotify;
import emu.lunarcore.server.packet.BasePacket;
import emu.lunarcore.server.packet.CmdId;

public class PacketSyncRogueMiracleSelectInfoScNotify extends BasePacket {

    public PacketSyncRogueMiracleSelectInfoScNotify(RogueMiracleSelectMenu miracleSelect) {
        super(CmdId.SyncRogueMiracleSelectInfoScNotify);
        
        var data = SyncRogueMiracleSelectInfoScNotify.newInstance()
                .setMiracleSelectInfo(miracleSelect.toProto());
        
        this.setData(data);
    }
}
