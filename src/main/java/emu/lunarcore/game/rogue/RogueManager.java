package emu.lunarcore.game.rogue;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import emu.lunarcore.GameConstants;
import emu.lunarcore.LunarCore;
import emu.lunarcore.data.GameData;
import emu.lunarcore.data.GameDepot;
import emu.lunarcore.data.excel.RogueTalentExcel;
import emu.lunarcore.game.player.BasePlayerManager;
import emu.lunarcore.game.player.Player;
import emu.lunarcore.game.player.lineup.PlayerLineup;
import emu.lunarcore.proto.ExtraLineupTypeOuterClass.ExtraLineupType;
import emu.lunarcore.proto.RogueAeonInfoOuterClass.RogueAeonInfo;
import emu.lunarcore.proto.RogueAreaInfoOuterClass.RogueAreaInfo;
import emu.lunarcore.proto.RogueAreaOuterClass.RogueArea;
import emu.lunarcore.proto.RogueAreaStatusOuterClass.RogueAreaStatus;
import emu.lunarcore.proto.RogueInfoDataOuterClass.RogueInfoData;
import emu.lunarcore.proto.RogueInfoOuterClass.RogueInfo;
import emu.lunarcore.proto.RogueScoreRewardInfoOuterClass.RogueScoreRewardInfo;
import emu.lunarcore.proto.RogueSeasonInfoOuterClass.RogueSeasonInfo;
import emu.lunarcore.proto.RogueTalentInfoOuterClass.RogueTalentInfo;
import emu.lunarcore.proto.RogueTalentOuterClass.RogueTalent;
import emu.lunarcore.proto.RogueTalentStatusOuterClass.RogueTalentStatus;
import emu.lunarcore.server.packet.CmdId;
import emu.lunarcore.server.packet.send.PacketLeaveRogueScRsp;
import emu.lunarcore.server.packet.send.PacketStartRogueScRsp;
import emu.lunarcore.server.packet.send.PacketSyncRogueFinishScNotify;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import lombok.Getter;
import us.hebi.quickbuf.RepeatedInt;

@Getter
public class RogueManager extends BasePlayerManager {
    private IntSet talents;

    public RogueManager(Player player) {
        super(player);
        this.talents = new IntOpenHashSet();
    }
    
    public boolean hasTalent(int talentId) {
        return this.getTalents().contains(talentId);
    }
    
    public boolean enableTalent(int talentId) {
        // Sanity check so we dont enable the same talent
        if (this.getTalents().contains(talentId)) {
            return false;
        }
        
        // Get talent excel
        RogueTalentExcel excel = GameData.getRogueTalentExcelMap().get(talentId);
        if (excel == null) return false;
        
        // Verify items
        if (!getPlayer().getInventory().verifyItems(excel.getCost())) {
            return false;
        }
        
        // Pay items
        getPlayer().getInventory().removeItemsByParams(excel.getCost());
        
        // Add talent
        RogueTalentData talent = new RogueTalentData(getPlayer(), excel.getTalentID());
        talent.save();
        
        return getTalents().add(talentId);
    }
    
    public void startRogue(int areaId, int aeonId, RepeatedInt avatarIdList) {
        // Make sure player already isnt in a rogue instance
        if (getPlayer().getRogueInstance() != null) {
            getPlayer().sendPacket(new PacketStartRogueScRsp());
            return;
        }
        
        // Get excel
        var rogueAreaExcel = GameData.getRogueAreaExcelMap().get(areaId);
        if (rogueAreaExcel == null || !rogueAreaExcel.isValid()) {
            getPlayer().sendPacket(new PacketStartRogueScRsp());
            return;
        }
        
        var aeonExcel = GameData.getRogueAeonExcelMap().get(aeonId);
        
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
        RogueInstance instance = new RogueInstance(getPlayer(), rogueAreaExcel, aeonExcel);
        getPlayer().setRogueInstance(instance);
        instance.createBonusSelect(1);
        
        // Set starting SP
        boolean extraSP = this.hasTalent(32);
        
        // Reset hp/sp
        lineup.forEachAvatar(avatar -> {
            avatar.setCurrentHp(lineup, 10000);
            avatar.setCurrentSp(lineup, extraSP ? avatar.getMaxSp() : avatar.getMaxSp() / 2);
            
            instance.getBaseAvatarIds().add(avatar.getAvatarId());
        });
        lineup.setMp(5); // Set technique points
        
        // Set first lineup before we enter scenes
        getPlayer().getLineupManager().setCurrentExtraLineup(ExtraLineupType.LINEUP_ROGUE, false);

        // Enter rogue
        RogueRoomData room = instance.enterRoom(instance.getStartSiteId());
        
        if (room == null) {
            // Reset lineup/instance if entering scene failed
            getPlayer().getLineupManager().setCurrentExtraLineup(0, false);
            getPlayer().setRogueInstance(null);
            // Send error packet
            getPlayer().sendPacket(new PacketStartRogueScRsp());
            return;
        }
        
        // Done
        getPlayer().sendPacket(new PacketStartRogueScRsp(getPlayer()));
    }
    
    public void leaveRogue() {
        if (getPlayer().getRogueInstance() == null) {
            getPlayer().getSession().send(CmdId.LeaveRogueScRsp);
            return;
        }
        
        // Clear rogue instance
        getPlayer().setRogueInstance(null);
        
        // Leave scene
        getPlayer().getLineupManager().setCurrentExtraLineup(0, false);
        getPlayer().enterScene(GameConstants.ROGUE_ENTRANCE, 0, false); // Make sure we dont send an enter scene packet here
        
        // Send packet
        getPlayer().sendPacket(new PacketLeaveRogueScRsp(this.getPlayer()));
    }
    
    public void quitRogue() {
        if (getPlayer().getRogueInstance() == null) {
            getPlayer().getSession().send(CmdId.QuitRogueScRsp);
            return;
        }
        
        getPlayer().getRogueInstance().onFinish();
        
        getPlayer().getSession().send(CmdId.QuitRogueScRsp);
        getPlayer().getSession().send(new PacketSyncRogueFinishScNotify(getPlayer()));
        
        // This isnt correct behavior, but it does the job
        this.leaveRogue();
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
                .setHasTakenInitialScore(true)
                .setBeginTime(beginTime)
                .setEndTime(endTime);
        
        var season = RogueSeasonInfo.newInstance()
                .setBeginTime(beginTime)
                .setSeasonId(seasonId)
                .setEndTime(endTime);
        
        // Path resonance
        var aeonInfo = RogueAeonInfo.newInstance();
        
        aeonInfo.setIsUnlocked(false);
        
        if (this.hasTalent(1) || true) {  // Consider using a constant for this because talent is not working now
            aeonInfo = RogueAeonInfo.newInstance()
                    .setUnlockAeonNum(GameData.getRogueAeonExcelMap().size());
            
            for (var aeonExcel : GameData.getRogueAeonExcelMap().values()) {
                aeonInfo.addAeonIdList(aeonExcel.getAeonID());
            }
            aeonInfo.setIsUnlocked(true);
            //aeonInfo.setUnlockAeonEnhanceNum(3);  // guess
        }

        var data = RogueInfoData.newInstance()
            .setRogueScoreInfo(score)
            .setRogueAeonInfo(aeonInfo)
            .setRogueSeasonInfo(season);
        
        // Rogue data
        RogueInstance instance = this.getPlayer().getRogueInstance();
        
        // Add areas
        var areaInfo = RogueAreaInfo.newInstance();
        if (schedule != null) {
            for (int i = 0; i < schedule.getRogueAreaIDList().length; i++) {
                var excel = GameData.getRogueAreaExcelMap().get(schedule.getRogueAreaIDList()[i]);
                if (excel == null) continue;
                
                var area = RogueArea.newInstance()
                        .setAreaId(excel.getRogueAreaID())
                        .setRogueAreaStatus(RogueAreaStatus.ROGUE_AREA_STATUS_FIRST_PASS.getNumber());
                
                if (instance != null && excel == instance.getExcel()) {
                    area.setMapId(instance.getExcel().getMapId());
                    area.setCurReachRoomNum(instance.getCurrentRoomProgress());
                    //area.setRogueStatus(instance.getStatus());
                }
                
                //proto.addRogueAreaList(area);
                areaInfo.addRogueArea(area);
            }
        }
        data.setRogueAreaInfo(areaInfo);
        
        var proto = RogueInfo.newInstance()
            .setRogueInfoData(data);
        
        if (instance != null) {
            proto.setRogueCurrentInfo(instance.toProto());
        }
        
        return proto;
    }
    
    public RogueTalentInfo toTalentInfoProto() {
        var proto = RogueTalentInfo.newInstance();
        
        for (RogueTalentExcel excel : GameData.getRogueTalentExcelMap().values()) {
            var talent = RogueTalent.newInstance()
                    .setTalentId(excel.getTalentID());
            
            if (this.hasTalent(excel.getTalentID())) {
                talent.setStatus(RogueTalentStatus.ROGUE_TALENT_STATUS_ENABLE);
            } else {
                talent.setStatus(RogueTalentStatus.ROGUE_TALENT_STATUS_UNLOCK);
            }
            
            proto.addRogueTalent(talent);
        }
        
        return proto;
    }
    
    // Database
    
    public void loadFromDatabase() {
        // Load talent data
        var stream = LunarCore.getGameDatabase().getObjects(RogueTalentData.class, "ownerUid", this.getPlayer().getUid());
        
        stream.forEach(talent -> {
            this.getTalents().add(talent.getTalentId());
        });
    }
}
