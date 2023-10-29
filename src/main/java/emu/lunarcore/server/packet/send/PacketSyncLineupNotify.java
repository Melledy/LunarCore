package emu.lunarcore.server.packet.send;

import emu.lunarcore.game.player.lineup.PlayerLineup;
import emu.lunarcore.proto.SyncLineupNotifyOuterClass.SyncLineupNotify;
import emu.lunarcore.server.packet.BasePacket;
import emu.lunarcore.server.packet.CmdId;

public class PacketSyncLineupNotify extends BasePacket {

    public PacketSyncLineupNotify(PlayerLineup lineup) {
        super(CmdId.SyncLineupNotify);

        var data = SyncLineupNotify.newInstance()
                .setLineup(lineup.toProto());

        this.setData(data);
    }
}
