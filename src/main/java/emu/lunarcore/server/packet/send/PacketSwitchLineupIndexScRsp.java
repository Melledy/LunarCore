package emu.lunarcore.server.packet.send;

import emu.lunarcore.game.player.lineup.PlayerLineup;
import emu.lunarcore.proto.SwitchLineupIndexScRspOuterClass.SwitchLineupIndexScRsp;
import emu.lunarcore.server.packet.BasePacket;
import emu.lunarcore.server.packet.CmdId;

public class PacketSwitchLineupIndexScRsp extends BasePacket {

    public PacketSwitchLineupIndexScRsp(PlayerLineup lineup) {
        super(CmdId.SwitchLineupIndexScRsp);

        var data = SwitchLineupIndexScRsp.newInstance()
                .setIndex(lineup.getIndex());

        this.setData(data);
    }
}
