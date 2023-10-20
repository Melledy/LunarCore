package emu.lunarcore.server.packet.send;

import emu.lunarcore.game.player.Player;
import emu.lunarcore.proto.GetRogueInfoScRspOuterClass.GetRogueInfoScRsp;
import emu.lunarcore.server.packet.BasePacket;
import emu.lunarcore.server.packet.CmdId;

public class PacketGetRogueInfoScRsp extends BasePacket {

    public PacketGetRogueInfoScRsp(Player player) {
        super(CmdId.GetRogueInfoScRsp);
        
        var proto = GetRogueInfoScRsp.newInstance()
                .setRogueInfo(player.getRogueManager().toProto());
        
        this.setData(proto);
    }
}
