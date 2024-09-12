package emu.lunarcore.server.packet.send;

import emu.lunarcore.game.player.Player;
import emu.lunarcore.proto.GetPetDataScRspOuterClass.GetPetDataScRsp;
import emu.lunarcore.server.packet.BasePacket;
import emu.lunarcore.server.packet.CmdId;

public class PacketGetPetDataScRsp extends BasePacket {

    public PacketGetPetDataScRsp(Player player) {
        super(CmdId.GetPetDataScRsp);
        
        var data = GetPetDataScRsp.newInstance()
                .setCurPetId(player.getPetId());
        
        for (int id : player.getUnlocks().getPets()) {
            data.addPetIdList(id);
        }
        
        this.setData(data);
    }
}
