package emu.lunarcore.server.packet.send;

import java.util.*;
import emu.lunarcore.game.player.Player;
import emu.lunarcore.proto.DailyActiveInfoNotifyOuterClass.DailyActiveInfoNotify;
import emu.lunarcore.proto.DailyActivityInfoOuterClass.DailyActivityInfo;
import emu.lunarcore.server.packet.BasePacket;
import emu.lunarcore.server.packet.CmdId;

public class PacketDailyActiveInfoNotify extends BasePacket {

    public PacketDailyActiveInfoNotify(Player player) {
        super(CmdId.DailyActiveInfoNotify);

        List<Integer> questIdList = Arrays.asList(2100003,2100102,2100112,2100115,2100119,2100129);
        
        var data = DailyActiveInfoNotify.newInstance()
            .setDailyActivePoint(700);
        
        for (int questId : questIdList) {
            data.addDailyActiveQuestIdList(questId);
        }

        for (int level = 1; level <= 5; level++) {
            DailyActivityInfo levelInfo = DailyActivityInfo.newInstance()
                .setLevel(level)
                .setDailyActivePoint(level * 100)
                .setWorldLevel(player.getWorldLevel())
                .setIsHasTaken(true);

            data.addDailyActiveLevelList(levelInfo);
        }

        this.setData(data);
    }
}
