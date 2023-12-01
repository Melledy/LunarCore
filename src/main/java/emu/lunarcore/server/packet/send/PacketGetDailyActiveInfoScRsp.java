package emu.lunarcore.server.packet.send;

import java.util.*;
import emu.lunarcore.proto.GetDailyActiveInfoScRspOuterClass.GetDailyActiveInfoScRsp;
import emu.lunarcore.proto.DailyActivityInfoOuterClass.DailyActivityInfo;
import emu.lunarcore.game.player.Player;
import emu.lunarcore.server.packet.BasePacket;
import emu.lunarcore.server.packet.CmdId;

public class PacketGetDailyActiveInfoScRsp extends BasePacket {

    public PacketGetDailyActiveInfoScRsp(Player target) {
        super(CmdId.GetDailyActiveInfoScRsp);

        List<Integer> questIdList = Arrays.asList(2100003,2100102,2100112,2100115,2100119,2100129);
        
        var data = GetDailyActiveInfoScRsp.newInstance()
            .setDailyActivePoint(500);
        
        for (int questId : questIdList) {
            data.addDailyActiveQuestIdList(questId);
        }

        for (int level = 1; level <= 5; level++) {
            DailyActivityInfo levelInfo = DailyActivityInfo.newInstance()
                .setLevel(level)
                .setDailyActivePoint(level * 100)
                .setWorldLevel(target.getWorldLevel())
                .setIsHasTaken(true);

            data.addDailyActiveLevelList(levelInfo);
        }

        this.setData(data);
    }
}
