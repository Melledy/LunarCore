package emu.lunarcore.game.challenge;

import dev.morphia.annotations.Entity;
import emu.lunarcore.data.GameData;
import emu.lunarcore.data.excel.ChallengeExcel;
import emu.lunarcore.game.battle.Battle;
import emu.lunarcore.game.player.Player;
import emu.lunarcore.game.scene.Scene;
import emu.lunarcore.game.scene.entity.EntityMonster;
import emu.lunarcore.proto.BattleEndReasonOuterClass.BattleEndReason;
import emu.lunarcore.proto.BattleEndStatusOuterClass.BattleEndStatus;
import emu.lunarcore.proto.BattleStatisticsOuterClass.BattleStatistics;
import emu.lunarcore.proto.ChallengeInfoOuterClass.ChallengeInfo;
import emu.lunarcore.proto.ChallengeStatusOuterClass.ChallengeStatus;
import emu.lunarcore.proto.ExtraLineupTypeOuterClass.ExtraLineupType;
import emu.lunarcore.server.packet.send.PacketChallengeLineupNotify;
import emu.lunarcore.server.packet.send.PacketChallengeSettleNotify;
import emu.lunarcore.server.packet.send.PacketSyncLineupNotify;
import emu.lunarcore.util.Position;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import lombok.Getter;
import lombok.Setter;

@Getter @Entity(useDiscriminator = false)
public class ChallengeInstance {
    private transient Player player;
    private transient ChallengeExcel excel;
    private Position startPos;
    private Position startRot;
    
    private int challengeId;
    private int currentStage;
    private int currentExtraLineup;
    private int status;
    private boolean hasAvatarDied;
    
    @Setter private int savedMp;
    @Setter private int roundsLeft;
    @Setter private int stars;
    @Setter private int scoreStage1;
    @Setter private int scoreStage2;
    
    private IntList storyBuffs;
    
    @Deprecated // Morphia only
    public ChallengeInstance() {}

    public ChallengeInstance(Player player, ChallengeExcel excel) {
        this.player = player;
        this.excel = excel;
        this.challengeId = excel.getId();
        this.startPos = new Position();
        this.startRot = new Position();
        this.currentStage = 1;
        this.roundsLeft = getExcel().isStory() ? 5 : excel.getChallengeCountDown();
        this.setStatus(ChallengeStatus.CHALLENGE_DOING);
        this.setCurrentExtraLineup(ExtraLineupType.LINEUP_CHALLENGE);
    }
    
    private Scene getScene() {
        return this.getPlayer().getScene();
    }
    
    private int getChallengeId() {
        return this.getExcel().getId();
    }
    
    public boolean isStory() {
        return this.excel.isStory();
    }
    
    private void setStatus(ChallengeStatus status) {
        this.status = status.getNumber();
    }
    
    private void setCurrentExtraLineup(ExtraLineupType type) {
        this.currentExtraLineup = type.getNumber();
    }
    
    private int getRoundsElapsed() {
        return getExcel().getChallengeCountDown() - this.roundsLeft;
    }
    
    public int getTotalScore() {
        return this.scoreStage1 + this.scoreStage2;
    }

    public boolean isWin() {
        return status == ChallengeStatus.CHALLENGE_FINISH_VALUE;
    }
    
    public void addStoryBuff(int storyBuff) {
        // Add story buffs
        if (storyBuffs == null) {
            storyBuffs = new IntArrayList();
        }
        
        storyBuffs.add(storyBuff);
    }
    
    public void onBattleStart(Battle battle) {
        // Set cycle limit
        battle.setRoundsLimit(player.getChallengeInstance().getRoundsLeft());
        
        // Add story buffs
        if (this.getStoryBuffs() != null) {
            battle.addBuff(this.getExcel().getMazeBuffID());
            
            int buffId = this.getStoryBuffs().getInt(this.getCurrentStage() - 1);
            if (buffId != 0) {
                battle.addBuff(buffId);
            }
        }
        
        // Add story battle targets
        if (this.getExcel().getStoryExcel() != null) {
            // Add base score counter
            battle.addBattleTarget(1, 10001, this.getTotalScore());
            // Add battle targets from story excel
            for (int id : getExcel().getStoryExcel().getBattleTargetID()) {
                battle.addBattleTarget(5, id, this.getTotalScore());
            }
        }
    }
    
    public void onBattleFinish(Battle battle, BattleEndStatus result, BattleStatistics stats) {
        // Add challenge score
        if (this.isStory()) {
            // Calculate score for current stage
            int stageScore = stats.getChallengeScore() - this.getTotalScore();
            // Set score
            if (this.getCurrentStage() == 1) {
                this.scoreStage1 = stageScore;
            } else {
                this.scoreStage2 = stageScore;
            }
        }
        
        // Handle result
        switch (result) {
        case BATTLE_END_WIN:
            // Check if any avatar in the lineup has died
            battle.getLineup().forEachAvatar(avatar -> {
                if (avatar.getCurrentHp(battle.getLineup()) <= 0) {
                    hasAvatarDied = true;
                }
            });
            
            // Get monster count in stage
            long monsters = player.getScene().getEntities().values().stream().filter(e -> e instanceof EntityMonster).count();
            
            if (monsters == 0) {
                this.advanceStage();
            }
            
            // Calculate rounds left
            if (!this.isStory()) {
                this.roundsLeft = Math.min(Math.max(this.roundsLeft - stats.getRoundCnt(), 1), this.roundsLeft);
            }
            
            // Set saved technique points (This will be restored if the player resets the challenge)
            this.savedMp = player.getCurrentLineup().getMp();
            break;
        case BATTLE_END_QUIT:
            // Reset technique points and move back to start position
            var lineup = player.getCurrentLineup();
            lineup.setMp(this.savedMp);
            player.moveTo(this.getStartPos(), this.getStartRot());
            player.sendPacket(new PacketSyncLineupNotify(lineup));
            break;
        default:
            // Determine challenge result
            if (this.isStory() && stats.getEndReason() == BattleEndReason.BATTLE_END_REASON_TURN_LIMIT) {
                this.advanceStage();
            } else {
                // Fail challenge
                this.setStatus(ChallengeStatus.CHALLENGE_FAILED);
                // Send challenge result data
                player.sendPacket(new PacketChallengeSettleNotify(this));
            }
            break;
        }
    }
    
    private void advanceStage() {
        // Progress to the next stage
        if (this.currentStage >= excel.getStageNum()) {
            // Last stage
            this.setStatus(ChallengeStatus.CHALLENGE_FINISH);
            this.stars = this.calculateStars();
            // Save history
            player.getChallengeManager().addHistory(this.getChallengeId(), this.getStars(), this.getTotalScore());
            // Send challenge result data
            player.sendPacket(new PacketChallengeSettleNotify(this));
        } else {
            // Increment and reset stage
            this.currentStage++;
            // Load scene group for stage 2
            this.getScene().loadGroup(excel.getMazeGroupID2());
            // Change player line up
            this.setCurrentExtraLineup(ExtraLineupType.LINEUP_CHALLENGE_2);
            player.getLineupManager().setCurrentExtraLineup(this.getCurrentExtraLineup(), true);
            player.sendPacket(new PacketChallengeLineupNotify(this.getCurrentExtraLineup()));
            this.savedMp = player.getCurrentLineup().getMp();
            // Move player
            player.moveTo(this.getStartPos(), this.getStartRot());
        }
    }

    public void onUpdate() {
        // End challenge if its done
        if (status != ChallengeStatus.CHALLENGE_DOING_VALUE) {
            getPlayer().setChallengeInstance(null);
        }
    }
    
    public int calculateStars() {
        int[] targets = getExcel().getChallengeTargetID();
        int stars = 0;
        
        for (int i = 0; i < targets.length; i++) {
            var target = GameData.getChallengeTargetExcelMap().get(targets[i]);
            if (target == null) continue;
            
            switch (target.getChallengeTargetType()) {
                case ROUNDS_LEFT:
                    if (this.getRoundsLeft() >= target.getChallengeTargetParam1()) {
                        stars += (1 << i);
                    }
                    break;
                case DEAD_AVATAR:
                    if (!this.hasAvatarDied) {
                        stars += (1 << i);
                    }
                    break;
                case TOTAL_SCORE:
                    if (this.getTotalScore() >= target.getChallengeTargetParam1()) {
                        stars += (1 << i);
                    }
                    break;
                default:
                    break;
            }
        }
        
        return Math.min(stars, 7);
    }

    public boolean validate(Player player) {
        if (this.player == null) {
            this.player = player;
            // Force extra lineup type to be set during login
            this.player.getLineupManager().setCurrentExtraLineup(this.getCurrentExtraLineup(), false);
        }
        
        this.excel = GameData.getChallengeExcelMap().get(this.challengeId);
        return this.excel != null;
    }
    
    public ChallengeInfo toProto() {
        var proto = ChallengeInfo.newInstance()
                .setChallengeId(this.getExcel().getId())
                .setStatusValue(this.getStatus())
                .setScore(this.getScoreStage1())
                .setScoreTwo(this.getScoreStage2())
                .setRoundCount(this.getRoundsElapsed())
                .setExtraLineupTypeValue(this.getCurrentExtraLineup());
        
        if (this.getStoryBuffs() != null) {
            int buffId = this.getStoryBuffs().getInt(this.getCurrentStage() - 1);
            proto.getMutableStoryInfo().getMutableCurStoryBuffs().addBuffList(buffId);
        }
        
        return proto;
    }
}