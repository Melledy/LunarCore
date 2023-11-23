package emu.lunarcore.server.packet.send;

import emu.lunarcore.game.player.Player;
import emu.lunarcore.proto.GetPlayerDetailInfoScRspOuterClass.GetPlayerDetailInfoScRsp;
import emu.lunarcore.server.packet.BasePacket;
import emu.lunarcore.server.packet.CmdId;

public class PacketGetPlayerDetailInfoScRsp extends BasePacket {

    public PacketGetPlayerDetailInfoScRsp(Player player) {
        super(CmdId.GetPlayerDetailInfoScRsp);

        var data = GetPlayerDetailInfoScRsp.newInstance();
        
        if (player != null) {
            data.setPlayerDetailInfo(player.toDetailInfo());
        } else {
            data.setRetcode(1);
        }

        this.setData(data);
    }
}
