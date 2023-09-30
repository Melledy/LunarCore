package emu.lunarcore.server.packet.send;

import emu.lunarcore.game.player.Player;
import emu.lunarcore.proto.SetSignatureScRspOuterClass.SetSignatureScRsp;
import emu.lunarcore.server.packet.BasePacket;
import emu.lunarcore.server.packet.CmdId;

public class PacketSetSignatureScRsp extends BasePacket {

    public PacketSetSignatureScRsp(Player player) {
        super(CmdId.SetSignatureScRsp);
        
        var data = SetSignatureScRsp.newInstance()
                .setSignature(player.getSignature());
        
        this.setData(data);
    }
}
