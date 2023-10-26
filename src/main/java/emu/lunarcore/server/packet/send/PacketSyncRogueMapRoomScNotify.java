package emu.lunarcore.server.packet.send;

import emu.lunarcore.game.rogue.RogueInstance;
import emu.lunarcore.game.rogue.RogueRoomData;
import emu.lunarcore.proto.SyncRogueMapRoomScNotifyOuterClass.SyncRogueMapRoomScNotify;
import emu.lunarcore.server.packet.BasePacket;
import emu.lunarcore.server.packet.CmdId;

public class PacketSyncRogueMapRoomScNotify extends BasePacket {

    public PacketSyncRogueMapRoomScNotify(RogueInstance rogue, RogueRoomData room) {
        super(CmdId.SyncRogueMapRoomScNotify);
        
        var data = SyncRogueMapRoomScNotify.newInstance()
                .setMapId(rogue.getExcel().getMapId())
                .setCurRoom(room.toProto());
        
        this.setData(data);
    }
}
