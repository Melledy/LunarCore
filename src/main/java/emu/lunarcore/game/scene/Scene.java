package emu.lunarcore.game.scene;

import java.util.ArrayList;
import java.util.List;

import emu.lunarcore.data.GameData;
import emu.lunarcore.data.config.*;
import emu.lunarcore.data.excel.MazePlaneExcel;
import emu.lunarcore.data.excel.SummonUnitExcel;
import emu.lunarcore.game.avatar.GameAvatar;
import emu.lunarcore.game.battle.Battle;
import emu.lunarcore.game.enums.PlaneType;
import emu.lunarcore.game.scene.entity.*;
import emu.lunarcore.game.scene.triggers.PropTrigger;
import emu.lunarcore.game.scene.triggers.PropTriggerType;
import emu.lunarcore.game.player.Player;
import emu.lunarcore.game.player.lineup.PlayerLineup;
import emu.lunarcore.proto.MotionInfoOuterClass.MotionInfo;
import emu.lunarcore.proto.SceneEntityGroupInfoOuterClass.SceneEntityGroupInfo;
import emu.lunarcore.proto.SceneEntityGroupInfoOuterClass.SceneEntityGroupInfo.GroupPropertyMapEntry;
import emu.lunarcore.proto.SceneGroupStateOuterClass.SceneGroupState;
import emu.lunarcore.proto.SceneInfoOuterClass.SceneInfo;
import emu.lunarcore.proto.MapPropDefInfoOuterClass.MapPropDefInfo;
import emu.lunarcore.server.game.Tickable;
import emu.lunarcore.server.packet.send.PacketActivateFarmElementScRsp;
import emu.lunarcore.server.packet.send.PacketRefreshTriggerByClientScNotify;
import emu.lunarcore.server.packet.send.PacketSceneGroupRefreshScNotify;
import emu.lunarcore.util.Position;
import it.unimi.dsi.fastutil.ints.*;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.Getter;
import lombok.Setter;
import us.hebi.quickbuf.RepeatedInt;

@Getter
public class Scene implements Tickable {
    private final Player player;
    private final MazePlaneExcel excel;
    private final FloorInfo floorInfo;
    private final SceneEntityLoader entityLoader;
    private final int planeId;
    private final int floorId;
    private int entryId;
    private int worldId;
    @Setter private int leaveEntryId;
    
    private int lastEntityId = 0;
    private boolean loaded = false;

    // Avatar entites
    private final IntSet avatarEntityIds;
    private final Int2ObjectMap<GameAvatar> avatars;
    private EntitySummonUnit playerSummon;
    
    // Other entities
    private final Int2ObjectMap<GameEntity> entities;
    private final Int2ObjectMap<SceneGroup> groups;

    // Marked chests
    private Int2ObjectMap<List<MapPropDefInfo>> markedChests;
    
    // Cache
    private List<PropTrigger> triggers;
    private List<EntityProp> healingSprings;
    private List<Integer> sceneSubMissions;
    private List<Integer> sceneMainMissions;
    
    public Scene(Player player, MazePlaneExcel excel, int floorId) {
        this.player = player;
        this.excel = excel;
        this.planeId = excel.getPlaneID();
        this.floorId = floorId;

        // Setup avatars
        this.avatarEntityIds = new IntOpenHashSet();
        this.avatars = new Int2ObjectOpenHashMap<>();
        this.entities = new Int2ObjectOpenHashMap<>();
        this.groups = new Int2ObjectOpenHashMap<>();
        
        this.healingSprings = new ObjectArrayList<>();
        this.triggers = new ObjectArrayList<>();

        this.markedChests = new Int2ObjectOpenHashMap<>();

        this.sceneSubMissions = new ObjectArrayList<>();
        this.sceneMainMissions = new ObjectArrayList<>();

        // Set world id
        if (this.getExcel().getPlaneType() == PlaneType.Train) {
            this.worldId = player.getWorldId();
        } else {
            this.worldId = this.getExcel().getWorldID();
        }
        
        // Use singleton to avoid allocating memory for a new entity loader everytime we create a scene
        this.entityLoader = getExcel().getPlaneType().getSceneEntityLoader();

        // Add avatar entities
        PlayerLineup lineup = getPlayer().getCurrentLineup();

        for (int avatarId : lineup.getAvatars()) {
            GameAvatar avatar = getPlayer().getAvatarById(avatarId);
            if (avatar == null) continue;

            this.avatars.put(avatarId, avatar);

            // Add entity id
            avatar.setEntityId(this.getNextEntityId());
            this.avatarEntityIds.add(avatar.getEntityId());
        }
        
        // Set floor info
        this.floorInfo = GameData.getFloorInfo(this.planeId, this.floorId);
        if (floorInfo == null) return;
        
        // Spawn entities from groups
        this.getPlaneType().getSceneEntityLoader().onSceneLoad(this);
    }
    
    public PlaneType getPlaneType() {
        return this.getExcel().getPlaneType();
    }
    
    public void loadGroup(int groupId) {
        GroupInfo group = getFloorInfo().getGroups().get(groupId);
        if (group != null) {
            this.loadGroup(group);
        }
    }
    
    public void loadGroup(GroupInfo group) {
        // Add monsters
        if (group.getMonsterList() != null && group.getMonsterList().size() > 0) {
            for (MonsterInfo monsterInfo : group.getMonsterList()) {
                try {
                    EntityMonster monster = this.getEntityLoader().loadMonster(this, group, monsterInfo);
                    this.addEntity(monster, this.isLoaded());
                } catch (Exception e) {
                    // Ignored
                }
            }
        }
        
        // Add props
        if (group.getPropList() != null && group.getPropList().size() > 0) {
            for (PropInfo propInfo : group.getPropList()) {
                try {
                    EntityProp prop = this.getEntityLoader().loadProp(this, group, propInfo);
                    this.addEntity(prop, this.isLoaded());
                } catch (Exception e) {
                    // Ignored
                }
            }
        }
        
        // Add NPCs
        if (group.getNPCList() != null && group.getNPCList().size() > 0) {
            for (NpcInfo npcInfo : group.getNPCList()) {
                try {
                    EntityNpc npc = this.getEntityLoader().loadNpc(this, group, npcInfo);
                    this.addEntity(npc, this.isLoaded());
                } catch (Exception e) {
                    // Ignored
                }
            }
        }
        
        // Add group properties
        if (group.getGroupPropertyMap() != null && group.getGroupPropertyMap().size() > 0) {
            var sceneGroup = this.getGroups().computeIfAbsent(group.getId(), i -> new SceneGroup(i));
            
            for (var property : group.getGroupPropertyMap().values()) {
                sceneGroup.getProperties().put(property.getName(), property.getDefaultValue());
            }
        }
    }

    public void setEntryId(int entryId) {
        this.entryId = entryId;
    }

    private int getNextEntityId() {
        return ++lastEntityId;
    }
    
    public synchronized GameEntity getEntityById(int id) {
        return this.getEntities().get(id);
    }
    
    public synchronized <T extends GameEntity> List<T> getEntities(Class<T> entityType) {
        return this.getEntities().values()
                .stream()
                .filter(e -> entityType.isInstance(e))
                .map(entityType::cast)
                .toList();
    }
    
    public synchronized <T extends GameEntity> List<T> getEntitiesByGroup(Class<T> entityType, int groupId) {
        return this.getEntities().values()
                .stream()
                .filter(e -> entityType.isInstance(e) && e.getGroupId() == groupId)
                .map(entityType::cast)
                .toList();
    }

    public void syncLineup() {
        // Get current lineup
        PlayerLineup lineup = getPlayer().getLineupManager().getCurrentLineup();

        // Setup new avatars list
        var newAvatars = new Int2ObjectOpenHashMap<GameAvatar>();
        for (int avatarId : lineup.getAvatars()) {
            GameAvatar avatar = getPlayer().getAvatarById(avatarId);
            if (avatar == null) continue;

            newAvatars.put(avatarId, avatar);
        }

        // Clear entity id cache
        this.avatarEntityIds.clear();

        // Add/Remove
        List<GameAvatar> toAdd = new ArrayList<>();
        List<GameAvatar> toRemove = new ArrayList<>();

        for (var avatar : newAvatars.values()) {
            if (!this.avatars.containsKey(avatar.getAvatarId())) {
                toAdd.add(avatar);
                avatar.setEntityId(getNextEntityId());
            }

            // Add to entity id cache
            this.avatarEntityIds.add(avatar.getEntityId());
            
            // Clear avatar's buff
            avatar.getBuffs().clear();
        }

        for (var avatar : this.avatars.values()) {
            if (!newAvatars.containsKey(avatar.getAvatarId())) {
                toRemove.add(avatar);
                avatar.setEntityId(0);
            }
        }

        // Sync packet
        getPlayer().sendPacket(new PacketSceneGroupRefreshScNotify(this, toAdd, toRemove));
    }
    
    public boolean activateFarmElement(int entityId, int worldLevel) {
        GameEntity entity = this.getEntityById(entityId);
        if (entity == null) {
            player.sendPacket(new PacketActivateFarmElementScRsp());
            return false;
        }
        
        if (entity instanceof EntityMonster monster) {
            monster.setWorldLevel(worldLevel);
        }
        
        player.sendPacket(new PacketActivateFarmElementScRsp(entityId, worldLevel));
        return true;
    }
    
    // Summons
    
    public synchronized void summonUnit(GameAvatar caster, SummonUnitExcel excel, Position pos, Position rot, int duration) {
        // Remove previous summon
        this.removeSummonUnit();
        
        // Add a new summoned unit to scene
        var summon = new EntitySummonUnit(this, caster, excel, pos, rot);
        summon.setDuration(duration);
        
        this.addEntity(summon, true);
    }
    
    public synchronized void removeSummonUnit() {
        if (this.getPlayerSummon() != null) {
            this.removeEntity(this.getPlayerSummon());
        }
    }
    
    public void handleSummonUnitTriggers(int entityId, String name, MotionInfo motion, RepeatedInt targetIds) {
        // Get summon unit
        EntitySummonUnit summonUnit = null;
        
        var entity = this.getEntityById(entityId);
        if (entity instanceof EntitySummonUnit) {
            summonUnit = (EntitySummonUnit) entity;
        } else {
            return;
        }
        
        // Get trigger
        var trigger = summonUnit.getExcel().getInfo().getTriggerByName(name);
        if (trigger == null) return;
        
        // Get targets
        var targets = new ArrayList<GameEntity>();
        
        for (int targetId : targetIds) {
            var target = this.getEntityById(targetId);
            if (target != null) {
                targets.add(target);
            }
        }
        
        // Handle task actions
        for (var action : trigger.getActions()) {
            action.onCastHit(summonUnit.getCaster(), targets);
        }
        
        // Send packet
        this.getPlayer().sendPacket(new PacketRefreshTriggerByClientScNotify(entityId, name, targetIds));
    }
    
    public void destroyProp(EntityProp prop) {
        // TODO sanity check prop to make sure it can be destroyed
        this.removeEntity(prop);
    }
    
    /**
     * Returns the nearest spring (Space Anchor) to the player in the scene
     * @return
     */
    public EntityProp getNearestSpring() {
        return getNearestSpring(Long.MAX_VALUE);
    }

    /**
     * Returns the nearest spring (Space Anchor) to the player in the scene
     * @param minDistSq Only checks springs in below this distance
     * @return
     */
    public EntityProp getNearestSpring(long minDistSq) {
        EntityProp spring = null;
        long springDist = 0;
        
        for (EntityProp prop : this.getHealingSprings()) {
            long dist = getPlayer().getPos().getFast2dDist(prop.getPos());
            if (dist > minDistSq) continue;
            
            if (spring == null || dist < springDist) {
                spring = prop;
                springDist = dist;
            }
        }
        
        return spring;
    }
    
    public void invokePropTrigger(PropTriggerType type, int param1, int param2) {
        for (PropTrigger trigger : this.getTriggers()) {
            if (trigger.shouldRun(param1, param2)) {
                trigger.run(this);
            }
        }
    }

    public synchronized void addEntity(GameEntity entity) {
        this.addEntity(entity, false);
    }
    
    public synchronized void addEntity(GameEntity entity, boolean sendPacket) {
        // Sanity checks - Also dont add entity if it already exists
        if (entity == null || entity.getEntityId() != 0) {
            return;
        }
        
        // Check if any of the this.getEntities() is the entity to avoid dupes
        for (GameEntity e : this.getEntities().values()) {
            if (e.equals(entity)) {
                return;
            }
        }
        
        // Set entity id and add monster to entity map
        entity.setEntityId(this.getNextEntityId());
        this.getEntities().put(entity.getEntityId(), entity);
        
        // Entity add callback
        entity.onAdd();
        
        // Send packet
        if (sendPacket) {
            player.sendPacket(new PacketSceneGroupRefreshScNotify(this, entity, null));
        }
    }
    
    public synchronized void removeEntity(GameEntity entity) {
        removeEntity(entity.getEntityId());
    }
    
    public synchronized void removeEntity(int entityId) {
        GameEntity entity = this.getEntities().remove(entityId);

        if (entity != null) {
            // Run event
            entity.onRemove();
            // Send packet
            player.sendPacket(new PacketSceneGroupRefreshScNotify(this, null, entity));
            // Reset entity id
            entity.setEntityId(0);
        }
    }
    
    // Player events
    
    @Override
    public synchronized void onTick(long timestamp, long delta) {
        // Remove summoned unit if it expired
        if (this.getPlayerSummon() != null) {
            if (this.getPlayerSummon().isExpired()) {
                this.removeSummonUnit();
            }
        }
        // Tick entities
        for (GameEntity entity : this.getEntities().values()) {
            if (entity instanceof Tickable tickableEntity) {
                tickableEntity.onTick(timestamp, delta);
            }
        }
    }
    
    public void onBattleStart(Battle battle) {
        // Remove summoned unit
        if (this.getPlayerSummon() != null) {
            this.removeSummonUnit();
        }
    }

    private boolean isExistingExtraData(SceneInfo scenedata, String key) {
        for (var entry : scenedata.getFloorSavedValueMap()) {
            if (entry.getKey().equals(key)) {
                return true;
            }
        }
        return false;
    }
    
    private void enableExtraData(SceneInfo scenedata, String key, int value) {
        if (!isExistingExtraData(scenedata, key)) {
            var entry = SceneInfo.FloorSavedValueMapEntry.newInstance()
                    .setKey(key)
                    .setValue(value);
            
            scenedata.addFloorSavedValueMap(entry);
        }
    }
    
    // Marked chests
    
    public void addMapPropDefInfo(MapPropDefInfo defInfo, int funcId) {
        if (!this.markedChests.containsKey(funcId)) {
            this.markedChests.put(funcId, new ArrayList<>());
        }
        this.markedChests.get(funcId).add(defInfo);
    }

    public void resetMapPropDefInfo(int funcId) {
        this.markedChests.remove(funcId);
    }

    public void removeAllMapPropDefInfo() {
        this.markedChests.clear();
    }

    public void setMapPropDefInfoForFuncId(int funcId, List<MapPropDefInfo> defInfos) {
        this.markedChests.put(funcId, defInfos);
    }

    public List<MapPropDefInfo> getMapPropDefInfo(int funcId) {
        return this.markedChests.get(funcId);
    }

    public List<Integer> getFuncIdsForMapPropDef() {
        return this.markedChests.keySet().intStream().boxed().toList();
    }
    
    // Proto serialization
    
    public synchronized SceneInfo toProto() {
        // Set loaded flag
        this.loaded = true;

        // Proto
        var proto = SceneInfo.newInstance()
                .setWorldId(this.getWorldId())
                .setGameModeType(this.getExcel().getPlaneType().getVal())
                .setPlaneId(this.getPlaneId())
                .setFloorId(this.getFloorId())
                .setEntryId(this.getEntryId());
                //.setSceneMissionInfo(this.getSceneMissionInfos());
        
        for (var extradata : this.getFloorInfo().getExtraDatas()) {
            //LunarCore.getLogger().warn("[Saved Value] " + extradata.getName() + " : " + extradata.getMaxValue());
            this.enableExtraData(proto, extradata.getName(), extradata.getMaxValue());
        }
        
        // Get current lineup
        PlayerLineup lineup = getPlayer().getCurrentLineup();
        int leaderAvatarId = lineup.getAvatars().get(lineup.getLeader());

        // Sort entities into groups
        var groups = new Int2ObjectOpenHashMap<SceneEntityGroupInfo>();
        
        // Create player group
        var playerGroup = SceneEntityGroupInfo.newInstance();

        for (var avatar : avatars.values()) {
            // Serialize lineup avatar to proto
            var avatarInfo = avatar.toSceneEntityProto();

            // Set leader avatar id and map layer for main avatar
            if (leaderAvatarId == avatar.getAvatarId()) {
                proto.setLeaderEntityId(avatar.getEntityId());
                avatarInfo.getMutableActor().setMapLayer(1);
            }
            
            playerGroup.addEntityList(avatarInfo);
        }

        groups.put(0, playerGroup);

        // Add rest of the entities to groups
        for (var entity : getEntities().values()) {
            // Get scene group
            var group = groups.computeIfAbsent(entity.getGroupId(), i -> SceneEntityGroupInfo.newInstance().setGroupId(i));
            
            // Add to group
            group.addEntityList(entity.toSceneEntityProto());
        }
        
        // Add groups
        for (var sceneGroup : this.getGroups().values()) {
            // Get group
            var group = groups.get(sceneGroup.getId());
            
            if (group != null && sceneGroup.getProperties().size() > 0) {
                // Add group properties
                for (var entry : sceneGroup.getProperties().object2IntEntrySet()) {
                    var property = GroupPropertyMapEntry.newInstance()
                            .setKey(entry.getKey())
                            .setValue(entry.getIntValue());
                    
                    group.addGroupPropertyMap(property);
                }
            }
            
            // Add group states 
            if (sceneGroup.getState() != 0) {
                var state = SceneGroupState.newInstance()
                        .setGroupId(sceneGroup.getId())
                        .setState(sceneGroup.getState())
                        .setIsDefault(true);
                
                proto.addGroupStateList(state);  
            }
        }

        // Add group to proto
        for (var group : groups.values()) {
            proto.addEntityGroupList(group);
        }
        
        // Scene identifier
        proto.getMutableSceneIdentifier()
            .setFloorId(this.getFloorId());
        
        // Done
        return proto;
    }
}
