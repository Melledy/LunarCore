package emu.lunarcore.server.packet.send;

import emu.lunarcore.game.player.Player;
import emu.lunarcore.proto.EnterSceneByServerScNotifyOuterClass.EnterSceneByServerScNotify;
import emu.lunarcore.server.packet.BasePacket;
import emu.lunarcore.server.packet.CmdId;

public class PacketEnterSceneByServerScNotify extends BasePacket {

    public PacketEnterSceneByServerScNotify(Player player) {
        super(CmdId.EnterSceneByServerScNotify);

        var data = EnterSceneByServerScNotify.newInstance()
                .setLineup(player.getCurrentLineup().toProto())
                .setScene(player.getScene().toProto());
        
        this.setData(data);
    }
}
