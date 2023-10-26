package emu.lunarcore.server.packet.send;

import emu.lunarcore.game.player.Player;
import emu.lunarcore.game.rogue.RogueRoomData;
import emu.lunarcore.proto.EnterRogueMapRoomScRspOuterClass.EnterRogueMapRoomScRsp;
import emu.lunarcore.server.packet.BasePacket;
import emu.lunarcore.server.packet.CmdId;

public class PacketEnterRogueMapRoomScRsp extends BasePacket {

    public PacketEnterRogueMapRoomScRsp(Player player, RogueRoomData room) {
        super(CmdId.EnterRogueMapRoomScRsp);
        
        var data = EnterRogueMapRoomScRsp.newInstance()
                .setLineup(player.getCurrentLineup().toProto())
                .setScene(player.getScene().toProto())
                .setCurSiteId(room.getSiteId());
        
        this.setData(data);
    }
}
