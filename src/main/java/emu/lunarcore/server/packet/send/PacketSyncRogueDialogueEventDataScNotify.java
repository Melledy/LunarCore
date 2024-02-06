package emu.lunarcore.server.packet.send;

import emu.lunarcore.LunarCore;
import emu.lunarcore.proto.RogueDialogueEventOuterClass.RogueDialogueEvent;
import emu.lunarcore.proto.RogueDialogueEventParamOuterClass.RogueDialogueEventParam;
import emu.lunarcore.proto.SyncRogueDialogueEventDataScNotifyOuterClass.SyncRogueDialogueEventDataScNotify;
import emu.lunarcore.server.packet.BasePacket;
import emu.lunarcore.server.packet.CmdId;

import java.util.List;

public class PacketSyncRogueDialogueEventDataScNotify extends BasePacket {
    public PacketSyncRogueDialogueEventDataScNotify(RogueDialogueEvent event) {
        super(CmdId.SyncRogueDialogueEventDataScNotify);
        
        var proto = SyncRogueDialogueEventDataScNotify.newInstance()
            .addRogueDialogueEvent(event);

        this.setData(proto);
    }
    
    public PacketSyncRogueDialogueEventDataScNotify(int rogueNpcId, List<RogueDialogueEventParam> params, int eventId) {
        this(RogueDialogueEvent.newInstance()
            .setGameModeType(5)  // rogue explore
            .setNpcId(rogueNpcId)
            //.setEventUniqueId(eventId)
            .addAllRogueDialogueEventParam(params.toArray(RogueDialogueEventParam[]::new)));
    }
}
