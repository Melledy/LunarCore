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
public class ChallengeData {
    private final Player player;
    private final Scene scene;
    private final ChallengeExcel excel;
    private final Position startPos;
    private final Position startRot;
    
    private int currentStage;
    private ExtraLineupType currentExtraLineup;
    private ChallengeStatus status;
    
    @Setter private int roundsLimit;
    @Setter private int stars;

    public ChallengeData(Player player, ChallengeExcel excel) {
        this.player = player;
        this.scene = player.getScene();
        this.excel = excel;
        this.startPos = player.getPos().clone();
        this.startRot = player.getRot().clone();
        this.currentStage = 1;
        this.roundsLimit = excel.getChallengeCountDown();
        this.status = ChallengeStatus.CHALLENGE_DOING;
        this.currentExtraLineup = ExtraLineupType.LINEUP_CHALLENGE;
        
        // Setup first stage
        this.setupStage1();
    }
    
    private int getChallengeId() {
        return this.getExcel().getId();
    }
    
    private void setupStage1() {
        this.setupStage(
                excel.getMazeGroupID1(), 
                excel.getConfigList1(), 
                excel.getNpcMonsterIDList1(), 
                excel.getEventIDList1(),
                false
        );
    }
    
    private void setupStage2() {
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
            monster.setInstId(instId);
            monster.setEventId(eventId);
            monster.setOverrideStageId(eventId);
            monster.setGroupId(group.getId());
            monster.setWorldLevel(this.getPlayer().getWorldLevel());
            
            // Add to scene
            getScene().addEntity(monster, sendPacket);
        }
    }

    public boolean isWin() {
        return status == ChallengeStatus.CHALLENGE_FINISH;
    }
    
    public void onBattleStart(Battle battle) {
        battle.setRoundsLimit(player.getChallengeData().getRoundsLimit());
    }
    
    public void onBattleFinish(Battle battle, BattleEndStatus result, BattleStatistics stats) {
        if (result == BattleEndStatus.BATTLE_END_WIN) {
            // Get monster count in stage
            long monsters = player.getScene().getEntities().values().stream().filter(e -> e instanceof EntityMonster).count();
            
            if (monsters == 0) {
                // Progress to the next stage
                if (this.currentStage >= excel.getStageNum()) {
                    // Last stage
                    this.status = ChallengeStatus.CHALLENGE_FINISH;
                    this.stars = 7; // TODO calculate the right amount stars
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
        } else {
            // Fail challenge
            this.status = ChallengeStatus.CHALLENGE_FAILED;
        }
    }

    public void onUpdate() {
        // End challenge if its done
        if (status != ChallengeStatus.CHALLENGE_DOING) {
            getPlayer().setChallengeData(null);
        }
    }
    
    public ChallengeInfo toProto() {
        var proto = ChallengeInfo.newInstance()
                .setChallengeId(this.getExcel().getId())
                .setStatus(this.getStatus())
                .setExtraLineupType(this.getCurrentExtraLineup());
        
        return proto;
    }
}