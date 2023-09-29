package emu.lunarcore.server.packet.send;

import emu.lunarcore.game.player.Player;
import emu.lunarcore.proto.SetClientPausedScRspOuterClass.SetClientPausedScRsp;
import emu.lunarcore.server.packet.BasePacket;
import emu.lunarcore.server.packet.CmdId;

public class PacketSetClientPausedScRsp extends BasePacket {

    public PacketSetClientPausedScRsp(Player player) {
        super(CmdId.SetClientPausedScRsp);
        
        var data = SetClientPausedScRsp.newInstance()
                .setPaused(player.isPaused());
        
        this.setData(data);
    }
}
