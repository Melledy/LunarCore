package emu.lunarcore.server.packet.send;

import emu.lunarcore.data.GameData;
import emu.lunarcore.proto.GetQuestDataScRspOuterClass.GetQuestDataScRsp;
import emu.lunarcore.proto.QuestOuterClass.Quest;
import emu.lunarcore.proto.QuestStatusOuterClass.QuestStatus;
import emu.lunarcore.server.packet.BasePacket;
import emu.lunarcore.server.packet.CmdId;
import it.unimi.dsi.fastutil.ints.IntSet;

public class PacketGetQuestDataScRsp extends BasePacket {
    @SuppressWarnings("unused")
    private static IntSet finishedQuests = IntSet.of(2200506, 2200503, 2200504, 2200505, 4080203);

    public PacketGetQuestDataScRsp() {
        super(CmdId.GetQuestDataScRsp);

        int achievements = 0;
        
        var data = GetQuestDataScRsp.newInstance();

        for (var excel : GameData.getQuestExcelMap().values()) {
            var quest = Quest.newInstance()
                    .setId(excel.getQuestID())
                    .setStatus(QuestStatus.QUEST_DOING);
            
            // Achievement
            if (excel.getQuestType() == 4) {
                quest.setStatus(QuestStatus.QUEST_CLOSE)
                    .setFinishTime(10000L)
                    .setProgress(1);
                
                achievements++;
            }
            
            /*
            if (finishedQuests.contains(excel.getQuestID())) {
                quest.setStatus(QuestStatus.QUEST_FINISH);
            }
            */

            data.addQuestList(quest);
        }
        
        data.setTotalAchievementExp(achievements);

        this.setData(data);
    }
}
