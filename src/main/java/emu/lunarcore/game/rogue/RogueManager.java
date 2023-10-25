package emu.lunarcore.game.rogue;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import emu.lunarcore.GameConstants;
import emu.lunarcore.data.GameData;
import emu.lunarcore.data.GameDepot;
import emu.lunarcore.data.config.AnchorInfo;
import emu.lunarcore.data.excel.RogueRoomExcel;
import emu.lunarcore.game.player.BasePlayerManager;
import emu.lunarcore.game.player.Player;
import emu.lunarcore.game.player.PlayerLineup;
import emu.lunarcore.proto.ExtraLineupTypeOuterClass.ExtraLineupType;
import emu.lunarcore.proto.RogueAreaOuterClass.RogueArea;
import emu.lunarcore.proto.RogueAreaStatusOuterClass.RogueAreaStatus;
import emu.lunarcore.proto.RogueInfoDataOuterClass.RogueInfoData;
import emu.lunarcore.proto.RogueInfoOuterClass.RogueInfo;
import emu.lunarcore.proto.RogueScoreRewardInfoOuterClass.RogueScoreRewardInfo;
import emu.lunarcore.proto.RogueSeasonInfoOuterClass.RogueSeasonInfo;
import emu.lunarcore.server.packet.CmdId;
import emu.lunarcore.server.packet.send.PacketStartRogueScRsp;

import us.hebi.quickbuf.RepeatedInt;

public class RogueManager extends BasePlayerManager {

    public RogueManager(Player player) {
        super(player);
    }
    
    public void startRogue(int areaId, RepeatedInt avatarIdList) {
        // Get excel
        var excel = GameData.getRogueAreaExcelMap().get(areaId);
        if (excel == null || !excel.isValid()) {
            getPlayer().sendPacket(new PacketStartRogueScRsp());
            return;
        }
        
        // Replace lineup
        getPlayer().getLineupManager().replaceLineup(0, ExtraLineupType.LINEUP_ROGUE_VALUE, Arrays.stream(avatarIdList.array()).boxed().toList());
        // Get lineup
        PlayerLineup lineup = getPlayer().getLineupManager().getLineupByIndex(0, ExtraLineupType.LINEUP_ROGUE_VALUE);
        // Make sure this lineup has avatars set
        if (lineup.getAvatars().size() == 0) {
            getPlayer().sendPacket(new PacketStartRogueScRsp());
            return;
        }
        
        // Get entrance id
        RogueData data = new RogueData(getPlayer(), excel);
        int entranceId = data.getCurrentRoom().getRoomExcel().getMapEntrance();
        
        // Reset hp/sp
        lineup.forEachAvatar(avatar -> {
            avatar.setCurrentHp(lineup, 10000);
            avatar.setCurrentSp(lineup, avatar.getMaxSp());
            
            data.getBaseAvatarIds().add(avatar.getAvatarId());
        });
        lineup.setMp(5); // Set technique points
        
        // Set first lineup before we enter scenes
        getPlayer().getLineupManager().setCurrentExtraLineup(ExtraLineupType.LINEUP_ROGUE, false);

        // Enter scene
        boolean success = getPlayer().enterScene(entranceId, 0, false);
        if (!success) {
            // Clear extra lineup if entering scene failed
            getPlayer().getLineupManager().setCurrentExtraLineup(0, false);
            // Send error packet
            getPlayer().sendPacket(new PacketStartRogueScRsp());
            return;
        }

        // Load scene groups
        RogueRoomExcel roomExcel = data.getCurrentRoom().getExcel();
        for (var entry : roomExcel.getGroupWithContent().entrySet()) {
            getPlayer().getScene().loadGroup(entry.getKey());
        }
        
        // Move player to rogue start position
        AnchorInfo anchor = getPlayer().getScene().getFloorInfo().getAnchorInfo(roomExcel.getGroupID(), 1);
        if (anchor != null) {
            getPlayer().getPos().set(anchor.getPos());
            getPlayer().getRot().set(anchor.getRot());
        }
        
        // Set rogue data and send packet
        getPlayer().setRogueData(data);
        getPlayer().sendPacket(new PacketStartRogueScRsp(getPlayer()));
    }
    
    public void quitRogue() {
        if (getPlayer().getRogueData() == null) {
            getPlayer().getSession().send(CmdId.QuitRogueScRsp);
            return;
        }
        
        getPlayer().setRogueData(null);
        getPlayer().enterScene(GameConstants.ROGUE_LEAVE_ENTRANCE, 0, true); // Test
        getPlayer().getSession().send(CmdId.QuitRogueScRsp);
    }

    public RogueInfo toProto() {
        var schedule = GameDepot.getCurrentRogueSchedule();
        
        int seasonId = 0;
        long beginTime = (System.currentTimeMillis() / 1000) - TimeUnit.DAYS.toSeconds(1);
        long endTime = beginTime + TimeUnit.DAYS.toSeconds(8);
        
        if (schedule != null) {
            seasonId = schedule.getRogueSeason();
        }
        
        var score = RogueScoreRewardInfo.newInstance()
                .setPoolId(20 + getPlayer().getWorldLevel()) // TODO pool ids should not change when world level changes
                .setPoolRefreshed(true)
                .setHasTakenInitialScore(true);
        
        var season = RogueSeasonInfo.newInstance()
                .setBeginTime(beginTime)
                .setSeasonId(seasonId)
                .setEndTime(endTime);
        
        var data = RogueInfoData.newInstance()
                .setRogueScoreInfo(score)
                .setRogueSeasonInfo(season);
        
        var proto = RogueInfo.newInstance()
                .setRogueScoreInfo(score)
                .setRogueData(data)
                .setSeasonId(seasonId)
                .setBeginTime(beginTime)
                .setEndTime(endTime);
        
        // Rogue data
        RogueData curRogue = this.getPlayer().getRogueData();
        if (curRogue != null) {
            proto.setStatus(curRogue.getStatus());
            proto.setRogueProgress(this.getPlayer().getRogueData().toProto());
            proto.setRoomMap(proto.getRogueProgress().getRoomMap());
            
            for (int id : curRogue.getBaseAvatarIds()) {
                proto.addBaseAvatarIdList(id);
            }
        }
        
        // Add areas
        if (schedule != null) {
            for (int i = 0; i < schedule.getRogueAreaIDList().length; i++) {
                var excel = GameData.getRogueAreaExcelMap().get(schedule.getRogueAreaIDList()[i]);
                if (excel == null) continue;
                
                var area = RogueArea.newInstance()
                        .setAreaId(excel.getRogueAreaID())
                        .setRogueAreaStatus(RogueAreaStatus.ROGUE_AREA_STATUS_FIRST_PASS);
                
                if (curRogue != null && excel == curRogue.getExcel()) {
                    area.setMapId(curRogue.getExcel().getMapId());
                    area.setCurReachRoomNum(curRogue.getCurrentRoomProgress());
                    area.setRogueStatus(curRogue.getStatus());
                }
                
                proto.addRogueAreaList(area);
            }
        }
        
        return proto;
    }
}