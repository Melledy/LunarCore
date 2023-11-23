package emu.lunarcore.server.packet.send;

import java.util.Collection;

import emu.lunarcore.game.player.Player;
import emu.lunarcore.proto.SearchPlayerScRspOuterClass.SearchPlayerScRsp;
import emu.lunarcore.server.packet.BasePacket;
import emu.lunarcore.server.packet.CmdId;

public class PacketSearchPlayerScRsp extends BasePacket {

    public PacketSearchPlayerScRsp(Collection<Player> players) {
        super(CmdId.SearchPlayerScRsp);
        
        var data = SearchPlayerScRsp.newInstance();
        
        if (players != null && players.size() > 0) {
            for (Player player : players) {
                data.addSearchResultList(player.toSimpleInfo());
            }
        } else {
            data.setRetcode(3612);
        }
        
        this.setData(data);
    }
}
