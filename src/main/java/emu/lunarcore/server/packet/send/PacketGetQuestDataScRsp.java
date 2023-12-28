package emu.lunarcore.server.packet.send;

import emu.lunarcore.proto.GetQuestDataScRspOuterClass.GetQuestDataScRsp;
import emu.lunarcore.proto.QuestOuterClass.Quest;
import emu.lunarcore.proto.QuestStatusOuterClass.QuestStatus;
import emu.lunarcore.server.packet.BasePacket;
import emu.lunarcore.server.packet.CmdId;
import emu.lunarcore.data.GameData;

public class PacketGetQuestDataScRsp extends BasePacket {

    public PacketGetQuestDataScRsp() {
        super(CmdId.GetQuestDataScRsp);
        
        var data = GetQuestDataScRsp.newInstance();
            //.setTotalAchievementExp(69);

        for (var questExcel : GameData.getQuestExcelMap().values()) {
            var questItem = Quest.newInstance()
                .setId(questExcel.getQuestID())
                .setStatus(QuestStatus.QUEST_CLOSE)
                .setFinishTime(10000L)
                .setProgress(1);
            
            data.addQuestList(questItem);
        }

        this.setData(data);
    }
}
