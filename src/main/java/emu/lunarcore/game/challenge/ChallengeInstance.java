package emu.lunarcore.game.challenge;

import emu.lunarcore.data.GameData;
import emu.lunarcore.data.config.GroupInfo;
import emu.lunarcore.data.config.MonsterInfo;
import emu.lunarcore.data.excel.ChallengeExcel;
import emu.lunarcore.data.excel.NpcMonsterExcel;
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

@Getter
public class ChallengeInstance {
    private final Player player;
    private final ChallengeExcel excel;
    private final Position startPos;
    private final Position startRot;
    
    private boolean hasAvatarDied;
    private int currentStage;
    private ExtraLineupType currentExtraLineup;
    private ChallengeStatus status;
    
    @Setter private int roundsLeft;
    @Setter private int stars;

    public ChallengeInstance(Player player, ChallengeExcel excel) {
        this.player = player;
        this.excel = excel;
        this.startPos = player.getPos().clone();
        this.startRot = player.getRot().clone();
        this.currentStage = 1;
        this.roundsLeft = excel.getChallengeCountDown();
        this.status = ChallengeStatus.CHALLENGE_DOING;
        this.currentExtraLineup = ExtraLineupType.LINEUP_CHALLENGE;
    }
    
    private Scene getScene() {
        return this.getPlayer().getScene();
    }
    
    private int getChallengeId() {
        return this.getExcel().getId();
    }
    
    protected void setupStage1() {
        this.setupStage(
                excel.getMazeGroupID1(), 
                excel.getConfigList1(), 
                excel.getNpcMonsterIDList1(), 
                excel.getEventIDList1(),
                false
        );
    }
    
    protected void setupStage2() {
        this.setupStage(
                excel.getMazeGroupID2(), 
                excel.getConfigList2(), 
                excel.getNpcMonsterIDList2(), 
                excel.getEventIDList2(),
                true
        );
    }
    
    private void setupStage(int groupId, int[] configs, int[] npcMonsters, int[] eventIds, boolean sendPacket) {
        // Load group
        GroupInfo group = getScene().getFloorInfo().getGroups().get(groupId);
        
        // Replace monsters in scene
        for (int i = 0; i < configs.length; i++) {
            // Setup vars
            int instId = configs[i];
            int npcMonster = npcMonsters[i];
            int eventId = eventIds[i];
            
            // Get monster info
            MonsterInfo monsterInfo = group.getMonsterById(instId);
            if (monsterInfo == null) continue;
            
            // Get excels from game data
            NpcMonsterExcel npcMonsterExcel = GameData.getNpcMonsterExcelMap().get(npcMonster);
            if (npcMonsterExcel == null) continue;
            
            // Create monster with excels
            EntityMonster monster = new EntityMonster(getScene(), npcMonsterExcel, monsterInfo.getPos());
            monster.getRot().setY((int) (monsterInfo.getRotY() * 1000f));
            monster.setGroupId(group.getId());
            monster.setInstId(instId);
            monster.setEventId(eventId);
            monster.setOverrideStageId(eventId);
            monster.setWorldLevel(this.getPlayer().getWorldLevel());
            
            // Add to scene
            getScene().addEntity(monster, sendPacket);
        }
    }
    
    private int getRoundsElapsed() {
        return getExcel().getChallengeCountDown() - this.roundsLeft;
    }

    public boolean isWin() {
        return status == ChallengeStatus.CHALLENGE_FINISH;
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
                    this.status = ChallengeStatus.CHALLENGE_FINISH;
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
                    this.currentExtraLineup = ExtraLineupType.LINEUP_CHALLENGE_2;
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
            this.status = ChallengeStatus.CHALLENGE_FAILED;
        }
    }

    public void onUpdate() {
        // End challenge if its done
        if (status != ChallengeStatus.CHALLENGE_DOING) {
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
    
    public ChallengeInfo toProto() {
        var proto = ChallengeInfo.newInstance()
                .setChallengeId(this.getExcel().getId())
                .setStatus(this.getStatus())
                .setRoundCount(this.getRoundsElapsed())
                .setExtraLineupType(this.getCurrentExtraLineup());
        
        return proto;
    }
}