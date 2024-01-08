package emu.lunarcore.game.challenge;

import java.util.List;
import java.util.stream.Stream;

import emu.lunarcore.GameConstants;
import emu.lunarcore.LunarCore;
import emu.lunarcore.data.GameData;
import emu.lunarcore.data.excel.ChallengeExcel;
import emu.lunarcore.game.inventory.GameItem;
import emu.lunarcore.game.player.BasePlayerManager;
import emu.lunarcore.game.player.Player;
import emu.lunarcore.game.player.lineup.PlayerLineup;
import emu.lunarcore.proto.ExtraLineupTypeOuterClass.ExtraLineupType;
import emu.lunarcore.proto.StartChallengeStoryBuffInfoOuterClass.StartChallengeStoryBuffInfo;
import emu.lunarcore.server.packet.Retcode;
import emu.lunarcore.server.packet.send.PacketStartChallengeScRsp;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import lombok.Getter;

@Getter
public class ChallengeManager extends BasePlayerManager {
    private Int2ObjectMap<ChallengeHistory> history;
    private Int2ObjectMap<ChallengeGroupReward> takenRewards;
    
    public ChallengeManager(Player player) {
        super(player);
        this.history = new Int2ObjectOpenHashMap<>();
        this.takenRewards = new Int2ObjectOpenHashMap<>();
    }
    
    public void startChallenge(int challengeId, StartChallengeStoryBuffInfo storyBuffs) {
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
            lineup.setMp(GameConstants.MAX_MP);
        }
        
        if (excel.getStageNum() >= 2) {
            // Get lineup
            PlayerLineup lineup = getPlayer().getLineupManager().getExtraLineupByType(ExtraLineupType.LINEUP_CHALLENGE_2_VALUE);
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
            lineup.setMp(GameConstants.MAX_MP);
        }

        // Set challenge data for player
        ChallengeInstance instance = new ChallengeInstance(getPlayer(), excel);
        getPlayer().setChallengeInstance(instance);

        // Set first lineup before we enter scenes
        getPlayer().getLineupManager().setCurrentExtraLineup(instance.getCurrentExtraLineup(), false);
        
        // Enter scene
        boolean success = getPlayer().enterScene(excel.getMapEntranceID(), 0, false);
        if (!success) {
            // Reset lineup/instance if entering scene failed
            getPlayer().getLineupManager().setCurrentExtraLineup(0, false);
            getPlayer().setChallengeInstance(null);
            // Send error packet
            getPlayer().sendPacket(new PacketStartChallengeScRsp(Retcode.CHALLENGE_NOT_EXIST));
            return;
        }
        
        // Save start positions
        instance.getStartPos().set(getPlayer().getPos());
        instance.getStartRot().set(getPlayer().getRot());
        instance.setSavedMp(getPlayer().getCurrentLineup().getMp());
        
        // Set story buffs
        if (excel.isStory() && storyBuffs != null) {
            instance.addStoryBuff(storyBuffs.getStoryBuffOne());
            instance.addStoryBuff(storyBuffs.getStoryBuffTwo());
        }

        // Send packet
        getPlayer().sendPacket(new PacketStartChallengeScRsp(getPlayer(), challengeId));
    }
    
    public synchronized void addHistory(int challengeId, int stars, int score) {
        // Dont write challenge history if the player didnt get any stars
        if (stars <= 0) return;
        
        // Get history info
        var info = this.getHistory().computeIfAbsent(challengeId, id -> new ChallengeHistory(getPlayer(), id));

        // Set
        info.setStars(stars);
        info.setScore(score);
        info.save();
    }
    
    public synchronized List<GameItem> takeRewards(int groupId, int starCount) {
        // Get excels
        var challengeGroup = GameData.getChallengeGroupExcelMap().get(groupId);
        if (challengeGroup == null) return null;
        
        var challengeReward = GameData.getChallengeRewardExcel(challengeGroup.getRewardLineGroupID(), starCount);
        if (challengeReward == null) return null;
        
        var rewardExcel = GameData.getRewardExcelMap().get(challengeReward.getRewardID());
        if (rewardExcel == null) return null;
        
        // Validate
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
        
        // Check if the player has enough stars
        if (totalStars < starCount) {
            return null;
        }
        
        // Get reward info
        var reward = this.getTakenRewards().computeIfAbsent(groupId, id -> new ChallengeGroupReward(getPlayer(), groupId));
        
        if (reward.hasTakenReward(starCount)) {
            return null;
        }
        
        reward.setTakenReward(starCount);
        
        // Add items to inventory
        return getPlayer().getInventory().addItemParams(rewardExcel.getRewards());
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
