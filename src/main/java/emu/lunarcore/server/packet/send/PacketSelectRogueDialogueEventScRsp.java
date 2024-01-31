package emu.lunarcore.server.packet.send;

import emu.lunarcore.data.GameData;
import emu.lunarcore.data.GameDepot;
import emu.lunarcore.data.excel.RogueNPCExcel;
import emu.lunarcore.game.player.Player;
import emu.lunarcore.game.scene.entity.EntityNpc;
import emu.lunarcore.proto.RogueDialogueEventOuterClass.RogueDialogueEvent;
import emu.lunarcore.proto.RogueDialogueEventParamOuterClass.RogueDialogueEventParam;
import emu.lunarcore.proto.SelectRogueDialogueEventScRspOuterClass.SelectRogueDialogueEventScRsp;
import emu.lunarcore.server.packet.BasePacket;
import emu.lunarcore.server.packet.CmdId;
import emu.lunarcore.util.Utils;

import java.util.ArrayList;

public class PacketSelectRogueDialogueEventScRsp extends BasePacket {

    public PacketSelectRogueDialogueEventScRsp(int dialogueEventId, int entityId, Player player) {
        super(CmdId.SelectRogueDialogueEventScRsp);
        
        var data = SelectRogueDialogueEventScRsp.newInstance()
                .setDialogueEventId(dialogueEventId);

        RogueNPCExcel rogueNpcExcel = Utils.randomElement(GameDepot.getRogueRandomNpcList());
        var params = new ArrayList<RogueDialogueEventParam>();
        var start = rogueNpcExcel.getId();
        while (true) {
            var event = GameData.getRogueDialogueEventList().get(start);
            if (event == null) break;
            params.add(RogueDialogueEventParam.newInstance()
                .setDialogueEventId(start)
                .setIsValid(true));
            start++;
        }
        var event = RogueDialogueEvent.newInstance()
            .setNpcId(((EntityNpc)player.getScene().getEntityById(entityId)).getRogueNpcId())
            .setGameModeType(5)
            .addAllNNOHLEAOJPP(dialogueEventId)
            .addAllRogueDialogueEventParam(params.toArray(RogueDialogueEventParam[]::new));
        
        data.setEventData(event);
        this.setData(data);
    }
}
