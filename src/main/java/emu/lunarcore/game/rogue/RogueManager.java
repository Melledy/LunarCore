package emu.lunarcore.game.rogue;

import java.util.concurrent.TimeUnit;

import emu.lunarcore.data.GameData;
import emu.lunarcore.data.GameDepot;
import emu.lunarcore.game.player.BasePlayerManager;
import emu.lunarcore.game.player.Player;
import emu.lunarcore.proto.RogueAreaOuterClass.RogueArea;
import emu.lunarcore.proto.RogueAreaStatusOuterClass.RogueAreaStatus;
import emu.lunarcore.proto.RogueInfoDataOuterClass.RogueInfoData;
import emu.lunarcore.proto.RogueInfoOuterClass.RogueInfo;
import emu.lunarcore.proto.RogueScoreRewardInfoOuterClass.RogueScoreRewardInfo;
import emu.lunarcore.proto.RogueSeasonInfoOuterClass.RogueSeasonInfo;

public class RogueManager extends BasePlayerManager {

    public RogueManager(Player player) {
        super(player);
    }

    public RogueInfo toProto() {
        var schedule = GameDepot.getCurrentRogueSchedule();
        
        int seasonId = 0;
        long beginTime = (System.currentTimeMillis() / 1000) - TimeUnit.DAYS.toSeconds(1);
        long endTime = beginTime + TimeUnit.DAYS.toSeconds(8);
        
        if (schedule != null) {
            seasonId = schedule.getId() % 100000;
        }
        
        var score = RogueScoreRewardInfo.newInstance()
                .setPoolId(20 + getPlayer().getWorldLevel()) // TODO pool ids should not change when world level changes
                .setPoolRefreshed(true)
                .setHasTakenInitialScore(true);
        
        var season = RogueSeasonInfo.newInstance()
                .setBeginTime(beginTime)
                .setRogueSeasonId(seasonId)
                .setEndTime(endTime);
        
        var data = RogueInfoData.newInstance()
                .setRogueScoreInfo(score)
                .setRogueSeasonInfo(season);
        
        var proto = RogueInfo.newInstance()
                .setRogueScoreInfo(score)
                .setRogueData(data)
                .setRogueSeasonId(seasonId)
                .setBeginTime(beginTime)
                .setEndTime(endTime);
        
        for (var excel : GameData.getRogueAreaExcelMap().values()) {
            var area = RogueArea.newInstance()
                    .setAreaId(excel.getRogueAreaID())
                    .setRogueAreaStatus(RogueAreaStatus.ROGUE_AREA_STATUS_FIRST_PASS);
            
            proto.addRogueAreaList(area);
        }
        
        return proto;
    }
}
