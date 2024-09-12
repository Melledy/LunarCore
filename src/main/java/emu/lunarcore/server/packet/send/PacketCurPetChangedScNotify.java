package emu.lunarcore.server.packet.send;

import emu.lunarcore.game.player.Player;
import emu.lunarcore.proto.CurPetChangedScNotifyOuterClass.CurPetChangedScNotify;
import emu.lunarcore.server.packet.BasePacket;
import emu.lunarcore.server.packet.CmdId;

public class PacketCurPetChangedScNotify extends BasePacket {

    public PacketCurPetChangedScNotify(Player player) {
        super(CmdId.CurPetChangedScNotify);
        
        var data = CurPetChangedScNotify.newInstance()
                .setCurPetId(player.getPetId());
        
        this.setData(data);
    }
}
