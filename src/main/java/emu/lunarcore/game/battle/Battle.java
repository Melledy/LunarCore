package emu.lunarcore.game.battle;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

import emu.lunarcore.GameConstants;
import emu.lunarcore.data.GameData;
import emu.lunarcore.data.excel.StageExcel;
import emu.lunarcore.game.avatar.GameAvatar;
import emu.lunarcore.game.inventory.GameItem;
import emu.lunarcore.game.player.Player;
import emu.lunarcore.game.player.lineup.PlayerLineup;
import emu.lunarcore.game.scene.entity.EntityMonster;
import emu.lunarcore.proto.BattleEndStatusOuterClass.BattleEndStatus;
import emu.lunarcore.proto.BattleEventBattleInfoOuterClass.BattleEventBattleInfo;
import emu.lunarcore.proto.BattleStatisticsOuterClass.BattleStatistics;
import emu.lunarcore.proto.BattleTargetListOuterClass.BattleTargetList;
import emu.lunarcore.proto.BattleTargetOuterClass.BattleTarget;
import emu.lunarcore.proto.SceneBattleInfoOuterClass.SceneBattleInfo;
import emu.lunarcore.proto.SceneBattleInfoOuterClass.SceneBattleInfo.BattleTargetInfoEntry;
import emu.lunarcore.util.Utils;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import lombok.Getter;
import lombok.Setter;

@Getter
public class Battle {
    private final int id;
    private final Player player;
    private final PlayerLineup lineup;
    private final List<EntityMonster> npcMonsters;
    private final List<MazeBuff> buffs;
    private final List<BattleMonsterWave> waves;
    private final List<GameItem> drops;
    private final long timestamp;
    
    private StageExcel stage; // Main battle stage
    private IntList battleEvents; // TODO maybe turn it into a map?
    private Int2ObjectMap<BattleTargetList> battleTargets; // TODO use custom battle target object as value type in case we need to save battles to the db
    
    // Internal battle data
    @Setter private BattleEndStatus result;
    @Setter private int staminaCost;
    @Setter private int roundsLimit;
    
    // Used for calculating cocoon/farm element drops
    @Setter private int mappingInfoId;
    @Setter private int worldLevel;
    @Setter private int cocoonWave;
    
    // OnFinish Callback
    @Setter private Consumer<BattleStatistics> onFinish;
    
    private Battle(Player player, PlayerLineup lineup) {
        this.id = player.getNextBattleId();
        this.player = player;
        this.lineup = lineup;
        this.npcMonsters = new ArrayList<>();
        this.buffs = new ArrayList<>();
        this.waves = new ArrayList<>();
        this.drops = new ArrayList<>();
        this.timestamp = System.currentTimeMillis();
    }
    
    public Battle(Player player, PlayerLineup lineup, StageExcel stage) {
        this(player, lineup, stage, true);
    }
    
    public Battle(Player player, PlayerLineup lineup, StageExcel stage, boolean loadStage) {
        this(player, lineup);
        this.stage = stage;
        
        if (loadStage) {
            this.loadStage(stage); 
        }
    }
    
    public Battle(Player player, PlayerLineup lineup, List<StageExcel> stages) {
        this(player, lineup);
        this.stage = stages.get(0);
        
        for (StageExcel stage : stages) {
            this.loadStage(stage);
        }
    }
    
    public Battle(Player player, PlayerLineup lineup, Collection<EntityMonster> npcMonsters) {
        this(player, lineup);
        
        // Parse npc monster
        for (EntityMonster npcMonster : npcMonsters) {
            // Add monster
            this.npcMonsters.add(npcMonster);

            // Check farm element
            if (npcMonster.getFarmElementId() != 0) {
                this.setMappingInfoId(npcMonster.getFarmElementId());
                this.setWorldLevel(npcMonster.getWorldLevel());
                this.setStaminaCost(GameConstants.FARM_ELEMENT_STAMINA_COST);
            }
            
            // Get stage
            StageExcel stage = GameData.getStageExcelMap().get(npcMonster.getStageId());
            if (stage == null) continue;
            
            // Set main battle stage if we havent already
            if (this.stage == null) {
                this.stage = stage;
            }
            
            // Create monster waves from stage
            this.loadStage(stage, npcMonster);
        }
    }
    
    private void loadStage(StageExcel stage) {
        this.loadStage(stage, null);
    }
    
    private void loadStage(StageExcel stage, EntityMonster npcMonster) {
        // Build monster waves
        for (IntList stageMonsterWave : stage.getMonsterWaves()) {
            // Create battle wave
            var wave = new BattleMonsterWave(stage);
            wave.getMonsters().addAll(stageMonsterWave);
            
            // Handle npc monster
            if (npcMonster != null) {
                // Set wave custom level
                wave.setCustomLevel(npcMonster.getCustomLevel());
                
                // Handle monster buffs
                npcMonster.applyBuffs(this, this.getWaves().size());
            }
            
            // Finally add wave to battle
            this.getWaves().add(wave);
        }
    }
    
    public IntList getBattleEvents() {
        if (this.battleEvents == null) {
            this.battleEvents = new IntArrayList();
        }
        return this.battleEvents;
    }
    
    public Int2ObjectMap<BattleTargetList> getBattleTargets() {
        if (this.battleTargets == null) {
            this.battleTargets = new Int2ObjectOpenHashMap<>();
        }
        return this.battleTargets;
    }
    
    public void addBattleTarget(int key, int targetId, int progress) {
        var list = getBattleTargets().computeIfAbsent(key, i -> BattleTargetList.newInstance());
        var battleTarget = BattleTarget.newInstance()
                .setId(targetId)
                .setProgress(progress);
        
        list.addBattleTargetList(battleTarget);
    }
    
    public void setCustomLevel(int level) {
        for (var wave : this.getWaves()) {
            wave.setCustomLevel(level);
        }
    }
    
    // Battle buffs
    
    public MazeBuff addBuff(int buffId) {
        return addBuff(buffId, -1, 0xffffffff);
    }
    
    public MazeBuff addBuff(int buffId, int ownerIndex) {
        return addBuff(buffId, ownerIndex, 0xffffffff);
    }
    
    public MazeBuff addBuff(int buffId, int ownerIndex, int waveFlag) {
        MazeBuff buff = new MazeBuff(buffId, 1, ownerIndex, waveFlag);
        return addBuff(buff);
    }
    
    public MazeBuff addBuff(MazeBuff buff) {
        this.buffs.add(buff);
        return buff;
    }
    
    public boolean hasBuff(int buffId) {
        return this.buffs.stream().filter(buff -> buff.getId() == buffId).findFirst().isPresent();
    }
    
    public void clearBuffs() {
        this.buffs.clear();
    }
    
    // Serialization
    
    public SceneBattleInfo toProto() {
        // Build battle info
        var proto = SceneBattleInfo.newInstance()
                .setBattleId(this.getId())
                .setStageId(this.getStage().getId())
                .setRoundsLimit(this.getRoundsLimit())
                .setLogicRandomSeed(Utils.randomRange(1, Short.MAX_VALUE))
                .setWorldLevel(player.getWorldLevel());

        // Add monster waves
        for (var wave : this.getWaves()) {
            proto.addMonsterWaveList(wave.toProto());
        }
        
        // Avatars
        for (int i = 0; i < lineup.getAvatars().size(); i++) {
            GameAvatar avatar = getPlayer().getAvatarById(lineup.getAvatars().get(i));
            if (avatar == null) continue;
            
            // Add to proto
            proto.addBattleAvatarList(avatar.toBattleProto(lineup, i));
            
            // Add buffs from avatars
            if (avatar.getBuffs().size() > 0) {
                for (var buffEntry : avatar.getBuffs().int2LongEntrySet()) {
                    // Check expiry for buff
                    if (buffEntry.getLongValue() < this.timestamp) {
                        continue;
                    }
                    
                    MazeBuff buff = this.addBuff(buffEntry.getIntKey(), i);
                    if (buff != null) {
                        buff.addTargetIndex(i);
                    }
                }
            }
        }
        
        // Apply food buffs to battle
        if (player.getFoodBuffs().size() > 0) {
            for (var buff : player.getFoodBuffs().values()) {
                this.addBuff(buff.getBuffId(), -1);
            }
        }
        
        // Buffs
        for (MazeBuff buff : this.getBuffs()) {
            proto.addBuffList(buff.toProto());
        }
        
        // Client turn snapshots
        if (this.battleEvents != null) {
            for (int id : this.battleEvents) {
                var event = BattleEventBattleInfo.newInstance()
                        .setBattleEventId(id);
                
                // Temp solution
                event.getMutableStatus().getMutableSpBar()
                        .setCurSp(10000)
                        .setMaxSp(10000);
                
                proto.addEventBattleInfoList(event);
            }
        }
        
        // Battle target map
        if (this.battleTargets != null) {
            // Build battle target map
            for (int i = 1; i <= 5; i++) {
                var battleTargetList = this.battleTargets.get(i);
                var battleTargetEntry = BattleTargetInfoEntry.newInstance().setKey(i);
                
                if (battleTargetList == null) {
                    battleTargetEntry.getMutableValue();
                } else {
                    battleTargetEntry.setValue(battleTargetList);
                }
                
                proto.addBattleTargetInfo(battleTargetEntry);
            }
        }
        
        return proto;
    }
}
