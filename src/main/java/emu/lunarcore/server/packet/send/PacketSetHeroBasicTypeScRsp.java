package emu.lunarcore.server.packet.send;

import emu.lunarcore.game.player.Player;
import emu.lunarcore.proto.SetHeroBasicTypeScRspOuterClass.SetHeroBasicTypeScRsp;
import emu.lunarcore.server.packet.BasePacket;
import emu.lunarcore.server.packet.CmdId;

public class PacketSetHeroBasicTypeScRsp extends BasePacket {

    public PacketSetHeroBasicTypeScRsp(Player player) {
        super(CmdId.SetHeroBasicTypeScRsp);
        
        var data = SetHeroBasicTypeScRsp.newInstance()
                .setBasicTypeValue(player.getCurBasicType());
        
        this.setData(data);
    }
}
