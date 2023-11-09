package emu.lunarcore.game.scene;

import java.util.ArrayList;
import java.util.List;

import emu.lunarcore.data.GameData;
import emu.lunarcore.data.config.*;
import emu.lunarcore.data.excel.MazePlaneExcel;
import emu.lunarcore.game.avatar.GameAvatar;
import emu.lunarcore.game.enums.PlaneType;
import emu.lunarcore.game.scene.entity.*;
import emu.lunarcore.game.scene.triggers.PropTrigger;
import emu.lunarcore.game.scene.triggers.PropTriggerType;
import emu.lunarcore.game.player.Player;
import emu.lunarcore.game.player.lineup.PlayerLineup;
import emu.lunarcore.proto.SceneEntityGroupInfoOuterClass.SceneEntityGroupInfo;
import emu.lunarcore.proto.SceneGroupStateOuterClass.SceneGroupState;
import emu.lunarcore.proto.SceneInfoOuterClass.SceneInfo;
import emu.lunarcore.server.packet.send.PacketActivateFarmElementScRsp;
import emu.lunarcore.server.packet.send.PacketSceneGroupRefreshScNotify;

import it.unimi.dsi.fastutil.ints.*;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.Getter;

@Getter
public class Scene {
    private final Player player;
    private final MazePlaneExcel excel;
    private final FloorInfo floorInfo;
    private final SceneEntityLoader entityLoader;
    private final int planeId;
    private final int floorId;
    private int entryId;
    
    private int lastEntityId = 0;
    private boolean loaded = false;

    // Avatar entites
    private final IntSet avatarEntityIds;
    private final Int2ObjectMap<GameAvatar> avatars;

    // Other entities
    private final Int2ObjectMap<GameEntity> entities;
    private final Int2IntMap groupStates;
    
    // Cache
    private List<PropTrigger> triggers;
    private List<EntityProp> healingSprings;
    
    public Scene(Player player, MazePlaneExcel excel, int floorId) {
        this.player = player;
        this.excel = excel;
        this.planeId = excel.getPlaneID();
        this.floorId = floorId;

        // Setup avatars
        this.avatarEntityIds = new IntOpenHashSet();
        this.avatars = new Int2ObjectOpenHashMap<>();
        this.entities = new Int2ObjectOpenHashMap<>();
        this.groupStates = new Int2IntOpenHashMap();
        
        this.healingSprings = new ObjectArrayList<>();
        this.triggers = new ObjectArrayList<>();
        
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
        getPlayer().sendPacket(new PacketSceneGroupRefreshScNotify(toAdd, toRemove));
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
    
    public void invokeTrigger(PropTriggerType type, int param1, int param2) {
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
        
        // Set entity id and add monster to entity map
        entity.setEntityId(this.getNextEntityId());
        this.getEntities().put(entity.getEntityId(), entity);
        
        // Entity add callback
        entity.onAdd();
        
        // Send packet
        if (sendPacket) {
            player.sendPacket(new PacketSceneGroupRefreshScNotify(entity, null));
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
            player.sendPacket(new PacketSceneGroupRefreshScNotify(null, entity));
            // Reset entity id
            entity.setEntityId(0);
        }
    }
    
    public synchronized SceneInfo toProto() {
        // Set loaded flag
        this.loaded = true;
        
        // Proto
        var proto = SceneInfo.newInstance()
                .setWorldId(this.getExcel().getWorldID())
                .setGameModeType(this.getExcel().getPlaneType().getVal())
                .setPlaneId(this.getPlaneId())
                .setFloorId(this.getFloorId())
                .setEntryId(this.getEntryId());

        // Get current lineup
        PlayerLineup lineup = getPlayer().getCurrentLineup();
        int leaderAvatarId = lineup.getAvatars().get(lineup.getLeader());

        // Sort entities into groups
        var groups = new Int2ObjectOpenHashMap<SceneEntityGroupInfo>();
        
        // Create player group
        var playerGroup = SceneEntityGroupInfo.newInstance();

        for (var avatar : avatars.values()) {
            playerGroup.addEntityList(avatar.toSceneEntityProto());

            if (leaderAvatarId == avatar.getAvatarId()) {
                proto.setLeaderEntityId(avatar.getEntityId());
            }
        }

        groups.put(0, playerGroup);

        // Add rest of the entities to groups
        for (var entity : getEntities().values()) {
            var group = groups.computeIfAbsent(entity.getGroupId(), i -> SceneEntityGroupInfo.newInstance().setGroupId(i));
            group.addEntityList(entity.toSceneEntityProto());
        }

        for (var group : groups.values()) {
            proto.addEntityGroupList(group);
        }
        
        // Add group states
        for (var entry : this.getGroupStates().int2IntEntrySet()) {
            var state = SceneGroupState.newInstance()
                    .setGroupId(entry.getIntKey())
                    .setState(entry.getIntValue())
                    .setIsDefault(true);
            
            proto.addGroupStateList(state);
        }

        // Done
        return proto;
    }
}
