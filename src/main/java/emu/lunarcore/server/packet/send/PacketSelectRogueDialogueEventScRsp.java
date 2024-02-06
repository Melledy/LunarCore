package emu.lunarcore.server.packet.send;

import emu.lunarcore.game.scene.entity.EntityNpc;
import emu.lunarcore.proto.DialogueResultOuterClass.DialogueResult;
import emu.lunarcore.proto.RogueDialogueEventOuterClass.RogueDialogueEvent;
import emu.lunarcore.proto.RogueDialogueEventParamOuterClass.RogueDialogueEventParam;
import emu.lunarcore.proto.SelectRogueDialogueEventScRspOuterClass.SelectRogueDialogueEventScRsp;
import emu.lunarcore.server.packet.BasePacket;
import emu.lunarcore.server.packet.CmdId;

public class PacketSelectRogueDialogueEventScRsp extends BasePacket {

    public PacketSelectRogueDialogueEventScRsp(int dialogueEventId, EntityNpc npc, int nextEventId) {
        super(CmdId.SelectRogueDialogueEventScRsp);
        
        var data = SelectRogueDialogueEventScRsp.newInstance()
                .setDialogueEventId(dialogueEventId);
        
        var instance = npc.getScene().getPlayer().getRogueInstance();
        
        var params = instance.curDialogueParams.get(npc.getRogueNpcId());
        if (params == null) {
            params = instance.setDialogueParams(npc.getRogueNpcId());
        }
        
        var event = RogueDialogueEvent.newInstance()
            .setNpcId(npc.getRogueNpcId())
            .setGameModeType(5)
            .addSelectEventId(dialogueEventId)
            //.setEventUniqueId(instance.getEventUniqueId())
            .addAllRogueDialogueEventParam(params.toArray(RogueDialogueEventParam[]::new));
        
        var l = DialogueResult.newInstance();
        for (var param : params) {
            l.addBLGIMDCNDHJ(param.getDialogueEventId());
        }
        if (nextEventId != 0) {
            l.addBLGIMDCNDHJ(nextEventId);
        }
        data.addDialogueResult(l);
        data.setEventData(event);
        
        this.setData(data);
    }
}
