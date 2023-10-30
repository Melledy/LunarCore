package emu.lunarcore.game.challenge;

import dev.morphia.annotations.Entity;

import emu.lunarcore.data.GameData;
import emu.lunarcore.data.excel.ChallengeExcel;
import emu.lunarcore.game.battle.Battle;
import emu.lunarcore.game.player.Player;
import emu.lunarcore.game.scene.Scene;
import emu.lunarcore.game.scene.entity.EntityMonster;
import emu.lunarcore.proto.BattleEndStatusOuterClass.BattleEndStatus;
import emu.lunarcore.proto.BattleStatisticsOuterClass.BattleStatistics;
import emu.lunarcore.proto.ChallengeInfoOuterClass.ChallengeInfo;
import emu.lunarcore.proto.ChallengeStatusOuterClass.ChallengeStatus;
import emu.lunarcore.proto.ExtraLineupTypeOuterClass.ExtraLineupType;
import emu.lunarcore.server.packet.send.PacketChallengeLineupNotify;
import emu.lunarcore.server.packet.send.PacketChallengeSettleNotify;
import emu.lunarcore.util.Position;

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
    
    @Setter private int roundsLeft;
    @Setter private int stars;
    
    @Deprecated // Morphia only
    public ChallengeInstance() {}

    public ChallengeInstance(Player player, ChallengeExcel excel) {
        this.player = player;
        this.excel = excel;
        this.challengeId = excel.getId();
        this.startPos = new Position();
        this.startRot = new Position();
        this.currentStage = 1;
        this.roundsLeft = excel.getChallengeCountDown();
        this.setStatus(ChallengeStatus.CHALLENGE_DOING);
        this.setCurrentExtraLineup(ExtraLineupType.LINEUP_CHALLENGE);
    }
    
    private Scene getScene() {
        return this.getPlayer().getScene();
    }
    
    private int getChallengeId() {
        return this.getExcel().getId();
    }
    
    private void setStatus(ChallengeStatus status) {
        this.status = status.getNumber();
    }
    
    private void setCurrentExtraLineup(ExtraLineupType type) {
        this.currentExtraLineup = type.getNumber();
    }
    
    protected void setupStage1() {
        this.getScene().loadGroup(excel.getMazeGroupID1());
    }
    
    protected void setupStage2() {
        this.getScene().loadGroup(excel.getMazeGroupID2());
    }
    
    private int getRoundsElapsed() {
        return getExcel().getChallengeCountDown() - this.roundsLeft;
    }

    public boolean isWin() {
        return status == ChallengeStatus.CHALLENGE_FINISH_VALUE;
    }
    
    public void onBattleStart(Battle battle) {
        battle.setRoundsLimit(player.getChallengeInstance().getRoundsLeft());
    }
    
    public void onBattleFinish(Battle battle, BattleEndStatus result, BattleStatistics stats) {
        if (result == BattleEndStatus.BATTLE_END_WIN) {
            // Check if any avatar in the lineup has died
            player.getCurrentLineup().forEachAvatar(avatar -> {
                if (!avatar.isAlive()) {
                    hasAvatarDied = true;
                }
            });
            
            // Get monster count in stage
            long monsters = player.getScene().getEntities().values().stream().filter(e -> e instanceof EntityMonster).count();
            
            if (monsters == 0) {
                // Progress to the next stage
                if (this.currentStage >= excel.getStageNum()) {
                    // Last stage
                    this.setStatus(ChallengeStatus.CHALLENGE_FINISH);
                    this.stars = this.calculateStars();
                    // Save history
                    player.getChallengeManager().addHistory(this.getChallengeId(), this.getStars());
                    // Send challenge result data
                    player.sendPacket(new PacketChallengeSettleNotify(this));
                } else {
                    // Increment and reset stage
                    this.currentStage++;
                    this.setupStage2();
                    // Change player line up
                    this.setCurrentExtraLineup(ExtraLineupType.LINEUP_CHALLENGE_2);
                    player.getLineupManager().setCurrentExtraLineup(this.getCurrentExtraLineup(), true);
                    player.sendPacket(new PacketChallengeLineupNotify(this.getCurrentExtraLineup()));
                    // Move player
                    player.moveTo(this.getStartPos(), this.getStartRot());
                }
            }
            
            // Calculate rounds left
            this.roundsLeft = Math.min(Math.max(this.roundsLeft - stats.getRoundCnt(), 0), this.roundsLeft);
        } else {
            // Fail challenge
            this.setStatus(ChallengeStatus.CHALLENGE_FAILED);
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
                default:
                    break;
            }
        }
        
        return Math.min(stars, 7);
    }

    public boolean validate(Player player) {
        if (this.player == null) {
            this.player = player;
        }
        
        this.excel = GameData.getChallengeExcelMap().get(this.challengeId);
        return this.excel != null;
    }
    
    public ChallengeInfo toProto() {
        var proto = ChallengeInfo.newInstance()
                .setChallengeId(this.getExcel().getId())
                .setStatusValue(this.getStatus())
                .setRoundCount(this.getRoundsElapsed())
                .setExtraLineupTypeValue(this.getCurrentExtraLineup());
        
        return proto;
    }
}