package emu.lunarcore.server.packet.send;

import emu.lunarcore.game.player.Player;
import emu.lunarcore.proto.SyncRogueFinishScNotifyOuterClass.SyncRogueFinishScNotify;
import emu.lunarcore.server.packet.BasePacket;
import emu.lunarcore.server.packet.CmdId;

public class PacketSyncRogueFinishScNotify extends BasePacket {

    public PacketSyncRogueFinishScNotify(Player player) {
        super(CmdId.SyncRogueFinishScNotify);
        
        var data = SyncRogueFinishScNotify.newInstance();
        
        if (player.getRogueInstance() != null) {
            data.setFinishInfo(player.getRogueInstance().toFinishInfoProto());
        }
        
        this.setData(data);
    }
}
