package emu.lunarcore.game.scene.entity;

import emu.lunarcore.data.config.GroupInfo;
import emu.lunarcore.data.config.MonsterInfo;
import emu.lunarcore.data.excel.NpcMonsterExcel;
import emu.lunarcore.game.battle.Battle;
import emu.lunarcore.game.scene.Scene;
import emu.lunarcore.game.scene.SceneBuff;
import emu.lunarcore.game.scene.triggers.PropTriggerType;
import emu.lunarcore.proto.MotionInfoOuterClass.MotionInfo;
import emu.lunarcore.proto.SceneEntityInfoOuterClass.SceneEntityInfo;
import emu.lunarcore.proto.SceneNpcMonsterInfoOuterClass.SceneNpcMonsterInfo;
import emu.lunarcore.server.game.Tickable;
import emu.lunarcore.server.packet.send.PacketSyncEntityBuffChangeListScNotify;
import emu.lunarcore.util.Position;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import lombok.Getter;
import lombok.Setter;

@Getter
public class EntityMonster implements GameEntity, Tickable {
    @Setter private NpcMonsterExcel excel;
    @Setter private int entityId;
    @Setter private int worldLevel;
    @Setter private int groupId;
    @Setter private int instId;
    @Setter private int eventId;
    
    private final Scene scene;
    private final Position pos;
    private final Position rot;
    
    private Int2ObjectMap<SceneBuff> buffs;
    @Setter private SceneBuff tempBuff;
    
    private int farmElementId;
    @Setter private int customStageId;
    @Setter private int customLevel;
    
    public EntityMonster(Scene scene, NpcMonsterExcel excel, GroupInfo group, MonsterInfo monsterInfo) {
        this.scene = scene;
        this.excel = excel;
        this.pos = monsterInfo.getPos().clone();
        this.rot = monsterInfo.getRot().clone();
        this.groupId = group.getId();
        this.instId = monsterInfo.getID();
        this.farmElementId = monsterInfo.getFarmElementID();
    }
    
    public boolean isFarmElement() {
        return this.farmElementId > 0;
    }
    
    public int getStageId() {
        if (this.customStageId == 0) {
            return (this.getEventId() * 10) + worldLevel;
        } else {
            return this.customStageId;
        }
    }
    
    public synchronized SceneBuff addBuff(int caster, int buffId, int duration) {
        if (this.buffs == null) {
            this.buffs = new Int2ObjectOpenHashMap<>();
        }
        
        // Create buff
        var buff = new SceneBuff(caster, buffId, duration);
        
        // Add to buff map
        this.buffs.put(buffId, buff);
        return buff;
    }
    
    public synchronized void applyBuffs(Battle battle, int waveIndex) {
        if (this.buffs != null) {
            for (var entry : this.buffs.int2ObjectEntrySet()) {
                // Check expiry for buff
                if (entry.getValue().isExpired(battle.getTimestamp())) {
                    continue;
                }
                
                // Add buff to battle
                this.applyBuff(battle, entry.getValue(), waveIndex);
            }
        }
        
        if (this.getTempBuff() != null) {
            this.applyBuff(battle, this.getTempBuff(), waveIndex);
            this.tempBuff = null;
        }
    }
    
    private boolean applyBuff(Battle battle, SceneBuff buff, int waveIndex) {
        // Get index of owner in lineup
        int ownerIndex = battle.getLineup().indexOf(buff.getCasterAvatarId());
        
        // Add buff to battle if owner exists
        if (ownerIndex != -1) {
            battle.addBuff(buff.getBuffId(), ownerIndex, 1 << waveIndex);
            return true;
        }
        
        // Failure
        return false;
    }
    
    @Override
    public void onRemove() {
        // Try to fire any triggers
        getScene().invokePropTrigger(PropTriggerType.MONSTER_DIE, this.getGroupId(), this.getInstId());
    }
    
    @Override
    public synchronized void onTick(long timestamp, long delta) {
        // Check if we need to remove any buffs
        if (this.buffs != null && this.buffs.size() > 0) {
            var it = this.buffs.values().iterator();
            
            while (it.hasNext()) {
                var buff = it.next();
                
                if (buff.isExpired(timestamp)) {
                    // Safely remove from iterator
                    it.remove();
                    
                    // Send packet to notify the client that we are removing the buff
                    getScene().getPlayer().sendPacket(new PacketSyncEntityBuffChangeListScNotify(this.getEntityId(), buff.getBuffId()));
                }
            }
        }
    }

    @Override
    public SceneEntityInfo toSceneEntityProto() {
        var monster = SceneNpcMonsterInfo.newInstance()
                .setWorldLevel(this.getWorldLevel())
                .setMonsterId(excel.getId())
                .setEventId(this.getEventId());

        var proto = SceneEntityInfo.newInstance()
                .setEntityId(this.getEntityId())
                .setGroupId(this.getGroupId())
                .setInstId(this.getInstId())
                .setMotion(MotionInfo.newInstance().setPos(getPos().toProto()).setRot(getRot().toProto()))
                .setNpcMonster(monster);

        return proto;
    }
}
