package emu.lunarcore.game.scene;

import java.util.ArrayList;
import java.util.List;

import emu.lunarcore.data.GameData;
import emu.lunarcore.data.config.*;
import emu.lunarcore.data.config.GroupInfo.GroupLoadSide;
import emu.lunarcore.data.excel.MazePlaneExcel;
import emu.lunarcore.data.excel.NpcMonsterExcel;
import emu.lunarcore.data.excel.PropExcel;
import emu.lunarcore.game.avatar.GameAvatar;
import emu.lunarcore.game.enums.PropState;
import emu.lunarcore.game.enums.PropType;
import emu.lunarcore.game.player.PlayerLineup;
import emu.lunarcore.game.scene.entity.*;
import emu.lunarcore.game.scene.triggers.PropTrigger;
import emu.lunarcore.game.scene.triggers.PropTriggerType;
import emu.lunarcore.game.player.Player;
import emu.lunarcore.proto.SceneEntityGroupInfoOuterClass.SceneEntityGroupInfo;
import emu.lunarcore.proto.SceneInfoOuterClass.SceneInfo;
import emu.lunarcore.server.packet.send.PacketActivateFarmElementScRsp;
import emu.lunarcore.server.packet.send.PacketSceneGroupRefreshScNotify;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.Getter;

@Getter
public class Scene {
    private final Player player;
    private final MazePlaneExcel excel;
    private final FloorInfo floorInfo;
    private final int planeId;
    private final int floorId;
    private int entryId;
    
    private int lastEntityId = 0;
    private boolean loaded = false;

    // Avatar entites
    private IntSet avatarEntityIds;
    private Int2ObjectMap<GameAvatar> avatars;

    // Other entities
    private Int2ObjectMap<GameEntity> entities;
    
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
        this.healingSprings = new ObjectArrayList<>();
        this.triggers = new ObjectArrayList<>();

        PlayerLineup lineup = getPlayer().getCurrentLineup();

        for (int avatarId : lineup.getAvatars()) {
            GameAvatar avatar = getPlayer().getAvatarById(avatarId);
            if (avatar == null) continue;

            this.avatars.put(avatarId, avatar);

            // Add entity id
            avatar.setEntityId(this.getNextEntityId());
            this.avatarEntityIds.add(avatar.getEntityId());
        }
        
        // Spawn monsters
        this.floorInfo = GameData.getFloorInfo(this.planeId, this.floorId);
        if (floorInfo == null) return;
        
        for (GroupInfo group : floorInfo.getGroups().values()) {
            // Skip non-server groups
            if (group.getLoadSide() != GroupLoadSide.Server) {
                continue;
            }
            
            // Add monsters
            if (group.getMonsterList() != null && group.getMonsterList().size() > 0) {
                for (MonsterInfo monsterInfo : group.getMonsterList()) {
                    // Get excels from game data
                    NpcMonsterExcel npcMonsterExcel = GameData.getNpcMonsterExcelMap().get(monsterInfo.getNPCMonsterID());
                    if (npcMonsterExcel == null) continue;
                    
                    // Create monster with excels
                    EntityMonster monster = new EntityMonster(this, npcMonsterExcel, monsterInfo.clonePos());
                    monster.getRot().setY((int) (monsterInfo.getRotY() * 1000f));
                    monster.setInstId(monsterInfo.getID());
                    monster.setEventId(monsterInfo.getEventID());
                    monster.setGroupId(group.getId());
                    monster.setWorldLevel(this.getPlayer().getWorldLevel());
                    
                    // Add to monsters
                    this.addEntity(monster);
                }
            }
            
            // Add props
            if (group.getPropList() != null && group.getPropList().size() > 0) {
                for (PropInfo propInfo : group.getPropList()) {
                    // Get prop excel
                    PropExcel propExcel = GameData.getPropExcelMap().get(propInfo.getPropID());
                    if (propExcel == null) {
                        continue;
                    }
                    
                    // Create prop from prop info
                    EntityProp prop = new EntityProp(this, propExcel, propInfo.clonePos());
                    prop.setState(propInfo.getState());
                    prop.getRot().set(
                            (int) (propInfo.getRotX() * 1000f),
                            (int) (propInfo.getRotY() * 1000f),
                            (int) (propInfo.getRotZ() * 1000f)
                    );
                    prop.setInstId(propInfo.getID());
                    prop.setGroupId(group.getId());
                    
                    // Hacky fixes
                    if (prop.getPropId() == 1003) {
                        // Open simulated universe
                        prop.setState(PropState.Open);
                    } else if (prop.getExcel().getPropType() == PropType.PROP_SPRING) {
                        // Cache teleport anchors
                        this.getHealingSprings().add(prop);
                    }
                    
                    // Add trigger
                    if (propInfo.getTrigger() != null) {
                        this.getTriggers().add(propInfo.getTrigger());
                    }
                    
                    // Add to monsters
                    this.addEntity(prop);
                }
            }
            
            // Add npcs
            if (group.getNPCList() != null && group.getNPCList().size() > 0) {
                for (NpcInfo npcInfo : group.getNPCList()) {
                    // Sanity check
                    if (!GameData.getNpcExcelMap().containsKey(npcInfo.getNPCID())) {
                        continue;
                    }
                    
                    // Dont spawn duplicate NPCs
                    boolean haseDuplicateNpcId = false;
                    for (GameEntity entity : this.getEntities().values()) {
                        if (entity instanceof EntityNpc eNpc && eNpc.getNpcId() == npcInfo.getNPCID()) {
                            haseDuplicateNpcId = true;
                            break;
                        }
                    }
                    if (haseDuplicateNpcId) continue;
                    
                    // Create npc from npc info
                    EntityNpc npc = new EntityNpc(this, npcInfo.getNPCID(), npcInfo.clonePos());
                    npc.getRot().setY((int) (npcInfo.getRotY() * 1000f));
                    npc.setInstId(npcInfo.getID());
                    npc.setGroupId(group.getId());
                    
                    // Add to monsters
                    this.addEntity(npc);
                }
            }
        }
        
        // Done
        this.loaded = true;
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
    
    // TODO
    public void fireTrigger(PropTriggerType type, int param1, int param2) {
        for (PropTrigger trigger : this.getTriggers()) {
            if (trigger.shouldRun(param1, param2)) {
                trigger.run(this);
            }
        }
    }

    public synchronized void addEntity(GameEntity entity) {
        // Dont add if monster id already exists
        if (entity.getEntityId() != 0) return;
        // Set entity id and add monster to entity map
        entity.setEntityId(this.getNextEntityId());
        this.getEntities().put(entity.getEntityId(), entity);
        // Entity add callback
        entity.onAdd();
    }
    
    public synchronized void removeEntity(GameEntity entity) {
        removeEntity(entity.getEntityId());
    }
    
    public synchronized void removeEntity(int entityId) {
        GameEntity entity = this.getEntities().remove(entityId);

        if (entity != null) {
            // Entity remove callback
            entity.onRemove();
            // Send packet
            player.sendPacket(new PacketSceneGroupRefreshScNotify(null, entity));
        }
    }
    
    public synchronized SceneInfo toProto() {
        // Proto
        var proto = SceneInfo.newInstance()
                .setWorldId(this.getExcel().getWorldID())
                .setGameModeType(this.getExcel().getPlaneType().getVal())
                .setPlaneId(this.getPlaneId())
                .setFloorId(this.getFloorId())
                .setEntryId(this.getEntryId());

        // Get current lineup
        PlayerLineup lineup = getPlayer().getCurrentLineup();
        int leaderAvatarId = lineup.getAvatars().get(getPlayer().getLineupManager().getCurrentLeader());

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

        // Done
        return proto;
    }
}
