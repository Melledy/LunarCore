package emu.lunarcore.game.challenge;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import emu.lunarcore.LunarCore;
import emu.lunarcore.data.GameData;
import emu.lunarcore.data.GameDepot;
import emu.lunarcore.data.excel.ChallengeExcel;
import emu.lunarcore.game.inventory.ItemParamMap;
import emu.lunarcore.game.player.BasePlayerManager;
import emu.lunarcore.game.player.Player;
import emu.lunarcore.game.player.lineup.PlayerLineup;
import emu.lunarcore.proto.ChallengePeakMobLineupInfoOuterClass.ChallengePeakMobLineupInfo;
import emu.lunarcore.proto.ExtraLineupTypeOuterClass.ExtraLineupType;
import emu.lunarcore.proto.ItemOuterClass.Item;
import emu.lunarcore.proto.TakenChallengeRewardInfoOuterClass.TakenChallengeRewardInfo;
import emu.lunarcore.server.packet.Retcode;
import emu.lunarcore.server.packet.send.PacketChallengePeakGroupDataUpdateScNotify;
import emu.lunarcore.server.packet.send.PacketStartChallengeScRsp;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import lombok.Getter;
import us.hebi.quickbuf.RepeatedInt;

@Getter
public class ChallengeManager extends BasePlayerManager {
    private Int2ObjectMap<ChallengeHistory> history;
    private Int2ObjectMap<ChallengeGroupReward> takenRewards;
    
    private RepeatedInt lastLineup1;
    private RepeatedInt lastLineup2;
    private int lastFirstHalfBuff;
    private int lastSecondHalfBuff;
    
    private Int2ObjectMap<IntList> challengePeakAvatars;
    
    public ChallengeManager(Player player) {
        super(player);
        this.history = new Int2ObjectOpenHashMap<>();
        this.takenRewards = new Int2ObjectOpenHashMap<>();
        this.challengePeakAvatars = new Int2ObjectOpenHashMap<>();
    }

    public void startChallenge(int challengeId, RepeatedInt lineup1, RepeatedInt lineup2, int firstHalfBuff, int secondHalfBuff) {
        // Get challenge excel
        ChallengeExcel excel = GameData.getChallengeExcelMap().get(challengeId);
        if (excel == null) {
            getPlayer().sendPacket(new PacketStartChallengeScRsp(Retcode.CHALLENGE_NOT_EXIST));
            return;
        }
        
        // Sanity check lineups
        if (excel.getStageNum() >= 1) {
            // Get lineup
            PlayerLineup lineup = getPlayer().getLineupManager().getExtraLineupByType(ExtraLineupType.LINEUP_CHALLENGE_VALUE);
            // Set lineup
            lineup.replace(Arrays.stream(lineup1.array()).boxed().toList());
            // Make sure this lineup has avatars set
            if (lineup.getAvatars().size() == 0) {
                getPlayer().sendPacket(new PacketStartChallengeScRsp(Retcode.CHALLENGE_LINEUP_EMPTY));
                return;
            }
            // Reset hp/sp
            lineup.forEachAvatar(avatar -> {
                avatar.setCurrentHp(lineup, 10000);
                avatar.setCurrentSp(lineup, avatar.getMaxSp() / 2);
            });
            // Set technique points to full
            lineup.setMp(lineup.getMaxMp());
        }
        
        if (excel.getStageNum() >= 2) {
            // Get lineup
            PlayerLineup lineup = getPlayer().getLineupManager().getExtraLineupByType(ExtraLineupType.LINEUP_CHALLENGE_2_VALUE);
            // Set lineup
            lineup.replace(Arrays.stream(lineup2.array()).boxed().toList());
            // Make sure this lineup has avatars set
            if (lineup.getAvatars().size() == 0) {
                getPlayer().sendPacket(new PacketStartChallengeScRsp(Retcode.CHALLENGE_LINEUP_EMPTY));
                return;
            }
            // Reset hp/sp
            lineup.forEachAvatar(avatar -> {
                avatar.setCurrentHp(lineup, 10000);
                avatar.setCurrentSp(lineup, avatar.getMaxSp() / 2);
            });
            // Set technique points to full
            lineup.setMp(lineup.getMaxMp());
        }

        // Set challenge data for player
        ChallengeInstance instance = new ChallengeInstance(getPlayer(), excel);
        getPlayer().setInstance(instance);

        // Set first lineup before we enter scenes
        getPlayer().getLineupManager().setCurrentExtraLineup(instance.getCurrentExtraLineup(), false);
        
        // Enter scene
        boolean success = getPlayer().enterScene(excel.getMapEntranceID(), 0, false);
        if (!success) {
            // Reset lineup/instance if entering scene failed
            getPlayer().getLineupManager().setCurrentExtraLineup(0, false);
            getPlayer().setInstance(null);
            // Send error packet
            getPlayer().sendPacket(new PacketStartChallengeScRsp(Retcode.CHALLENGE_NOT_EXIST));
            return;
        }
        
        // Save start positions
        instance.getStartPos().set(getPlayer().getPos());
        instance.getStartRot().set(getPlayer().getRot());
        instance.setSavedMp(getPlayer().getCurrentLineup().getMp());
        
        // Save latest lineup/buffs
        this.lastLineup1 = lineup1;
        this.lastLineup2 = lineup2;
        this.lastFirstHalfBuff = firstHalfBuff;
        this.lastSecondHalfBuff = secondHalfBuff;
        
        // Set story buffs
        if (excel.getType() != ChallengeType.MEMORY && (firstHalfBuff != 0 || secondHalfBuff != 0)) {
            // TODO: set story buffs for each stage individually
            if (firstHalfBuff != 0)
                instance.addBuff(firstHalfBuff);
            if (secondHalfBuff != 0)
                instance.addBuff(secondHalfBuff);
        }

        // Send packet
        getPlayer().sendPacket(new PacketStartChallengeScRsp(getPlayer(), challengeId, lineup1, lineup2));
    }
    
    public void setChallengePeakMobLineupAvatars(int groupId, Iterable<ChallengePeakMobLineupInfo> infos) {
        var group = GameData.getChallengePeakGroupExcelMap().get(groupId);
        if (group == null) {
            return;
        }
        
        // Clear avatars
        this.getChallengePeakAvatars().clear();
        
        for (var info : infos) {
            var lineup = new IntArrayList();
            
            for (int avatarId : info.getChallengeAvatarIdList()) {
                lineup.add(avatarId);
            }
            
            this.getChallengePeakAvatars().put(info.getChallengePeakId(), lineup);
        }
        
        // Update group
        this.getPlayer().sendPacket(new PacketChallengePeakGroupDataUpdateScNotify(groupId, this.getChallengePeakAvatars()));
    }
    
    public void startChallengePeak(int levelId) {
        var level = GameData.getChallengePeakExcelMap().get(levelId);
        if (level == null) {
            return;
        }
        
        var lineupAvatars = this.getChallengePeakAvatars().get(levelId);
        if (lineupAvatars == null) {
            return;
        }
        
        // Get lineup
        PlayerLineup lineup = getPlayer().getLineupManager().getExtraLineupByType(ExtraLineupType.LINEUP_CHALLENGE_VALUE);
        
        // Set lineup
        lineup.replace(lineupAvatars);
        
        // Make sure this lineup has avatars set
        if (lineup.getAvatars().size() == 0) {
            return;
        }
        
        // Reset hp/sp
        lineup.forEachAvatar(avatar -> {
            avatar.setCurrentHp(lineup, 10000);
            avatar.setCurrentSp(lineup, avatar.getMaxSp() / 2);
        });
        
        // Set technique points to full
        lineup.setMp(lineup.getMaxMp());
        
        // Set first lineup before we enter scenes
        getPlayer().getLineupManager().setCurrentExtraLineup(ExtraLineupType.LINEUP_CHALLENGE_VALUE, false);
        
        // TODO
    }
    
    public synchronized void addHistory(int challengeId, int stars, int score, boolean createExtraData) {
        // Dont write challenge history if the player didnt get any stars
        if (stars <= 0) return;
        
        // Get history info
        var info = this.getHistory().computeIfAbsent(challengeId, id -> new ChallengeHistory(getPlayer(), id));
        
        // Create extra data
        if (createExtraData) {
            info.getExtraData(); // Temporary solution TODO
        }

        // Set
        info.setStars(stars);
        info.setScore(score);
        info.save();
    }
    
    public synchronized List<TakenChallengeRewardInfo> takeRewards(int groupId) {
        // Get excels
        var challengeGroup = GameData.getChallengeGroupExcelMap().get(groupId);
        if (challengeGroup == null) return null;
        
        var challengeRewardLine = GameDepot.getChallengeRewardLines().get(challengeGroup.getRewardLineGroupID());
        if (challengeRewardLine == null) return null;
        
        // Get total stars
        int totalStars = 0;
        for (ChallengeHistory ch : this.getHistory().values()) {
            // Legacy compatibility
            if (ch.getGroupId() == 0) {
                var challengeExcel = GameData.getChallengeExcelMap().get(ch.getChallengeId());
                if (challengeExcel == null) continue;
                
                ch.setGroupId(challengeExcel.getGroupID());
                ch.save();
            }
            
            // Add total stars
            if (ch.getGroupId() == groupId) {
                totalStars += ch.getTotalStars();
            }
        }
        
        // Rewards
        List<TakenChallengeRewardInfo> rewardInfos = new ArrayList<>();
        ItemParamMap rewardItems = new ItemParamMap();
        
        // Get challenge rewards
        for (var challengeReward : challengeRewardLine) {
            // Check if we have enough stars to take this reward
            if (totalStars < challengeReward.getStarCount()) {
                continue;
            }
            
            // Get reward info
            var reward = this.getTakenRewards().computeIfAbsent(groupId, x -> new ChallengeGroupReward(getPlayer(), groupId));
            
            // Check if reward has been taken
            if (reward.hasTakenReward(challengeReward.getStarCount())) {
                continue;
            }
            
            // Set reward as taken
            reward.setTakenReward(challengeReward.getStarCount());
            
            // Get reward excel
            var rewardExcel = GameData.getRewardExcelMap().get(challengeReward.getRewardID());
            if (rewardExcel == null) continue;
            
            // Add rewards
            var proto = TakenChallengeRewardInfo.newInstance()
                    .setStarCount(challengeReward.getStarCount());
            
            // Create item param protos
            for (var entry : rewardExcel.getRewards().int2IntEntrySet()) {
                Item itemProto = Item.newInstance()
                        .setItemId(entry.getIntKey())
                        .setNum(entry.getIntValue());
                
                proto.getMutableRewardList().addItemList(itemProto);
            }

            rewardInfos.add(proto);
            
            // Add to reward item param map
            rewardItems.add(rewardExcel.getRewards());
        }
        
        // Add items to inventory
        getPlayer().getInventory().addItems(rewardItems);
        
        // Return reward infos
        return rewardInfos;
    }
    
    public void loadFromDatabase() {
        // Load challenge history
        Stream<ChallengeHistory> stream = LunarCore.getGameDatabase().getObjects(ChallengeHistory.class, "ownerUid", getPlayer().getUid());

        stream.forEach(info -> {
            this.getHistory().put(info.getChallengeId(), info);
        });
        
        // Load challenge rewards
        Stream<ChallengeGroupReward> stream2 = LunarCore.getGameDatabase().getObjects(ChallengeGroupReward.class, "ownerUid", getPlayer().getUid());

        stream2.forEach(info -> {
            this.getTakenRewards().put(info.getGroupId(), info);
        });
    }
}
