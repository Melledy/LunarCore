package emu.lunarcore.server.packet.send;

import emu.lunarcore.game.player.Player;
import emu.lunarcore.proto.StartRogueScRspOuterClass.StartRogueScRsp;
import emu.lunarcore.server.packet.BasePacket;
import emu.lunarcore.server.packet.CmdId;
import lombok.SneakyThrows;

public class PacketStartRogueScRsp extends BasePacket {

    public PacketStartRogueScRsp() {
        super(CmdId.StartRogueScRsp);
        
        var data = StartRogueScRsp.newInstance()
                .setRetcode(1);
        
        this.setData(data);
    }
    
    @SneakyThrows
    public PacketStartRogueScRsp(Player player) {
        super(CmdId.StartRogueScRsp);

        var data = StartRogueScRsp.newInstance()
                .setRogueInfo(player.getRogueManager().toProto())
                .setLineup(player.getCurrentLineup().toProto())
                .setScene(player.getScene().toProto());
        
        this.setData(data);
    }
}
