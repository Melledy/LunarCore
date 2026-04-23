package emu.lunarcore.server.packet.send;

import emu.lunarcore.game.gacha.GachaType;
import emu.lunarcore.game.player.Player;
import emu.lunarcore.proto.GetGachaCeilingScRspOuterClass.GetGachaCeilingScRsp;
import emu.lunarcore.server.packet.BasePacket;
import emu.lunarcore.server.packet.CmdId;

public class PacketGetGachaCeilingScRsp extends BasePacket {

    public PacketGetGachaCeilingScRsp(Player player) {
        super(CmdId.GetGachaCeilingScRsp);

        var proto = GetGachaCeilingScRsp.newInstance()
                .setGachaType(GachaType.Normal.getId())
                .setGachaCeiling(player.getGachaInfo().toGachaCeiling(player));

        this.setData(proto);
    }
}
