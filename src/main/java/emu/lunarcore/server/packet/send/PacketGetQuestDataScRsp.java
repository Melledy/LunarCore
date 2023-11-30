package emu.lunarcore.server.packet.send;

import emu.lunarcore.proto.GetQuestDataScRspOuterClass.GetQuestDataScRsp;
import emu.lunarcore.proto.QuestOuterClass.Quest;
import emu.lunarcore.proto.QuestOuterClass.Quest.QuestStatus;
import emu.lunarcore.server.game.GameSession;
import emu.lunarcore.server.packet.BasePacket;
import emu.lunarcore.server.packet.CmdId;
import emu.lunarcore.data.GameData;

public class PacketGetQuestDataScRsp extends BasePacket {

    public PacketGetQuestDataScRsp() {
        super(CmdId.GetQuestDataScRsp);

        var allIds = GameData.getAllQuestIds();

        var data = GetQuestDataScRsp.newInstance();
            //.setTotalAchievementExp(69);

        for (int questId : allIds) {
            var questItem = Quest.newInstance()
                .setId(questId)
                .setStatus(QuestStatus.QUEST_FINISH)
                .setFinishTime(10000L)
                .setProgress(1);
            data.addQuestList(questItem);
        }

        this.setData(data);
    }
}
