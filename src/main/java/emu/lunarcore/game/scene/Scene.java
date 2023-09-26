package emu.lunarcore.game.scene;

import java.util.ArrayList;
import java.util.List;

import emu.lunarcore.GameConstants;
import emu.lunarcore.data.GameData;
import emu.lunarcore.data.config.FloorInfo;
import emu.lunarcore.data.config.GroupInfo;
import emu.lunarcore.data.config.GroupInfo.GroupLoadSide;
import emu.lunarcore.data.config.MonsterInfo;
import emu.lunarcore.data.config.PropInfo;
import emu.lunarcore.data.excel.NpcMonsterExcel;
import emu.lunarcore.data.excel.StageExcel;
import emu.lunarcore.game.avatar.GameAvatar;
import emu.lunarcore.game.player.PlayerLineup;
import emu.lunarcore.game.player.Player;
import emu.lunarcore.proto.SceneEntityGroupInfoOuterClass.SceneEntityGroupInfo;
import emu.lunarcore.proto.SceneInfoOuterClass.SceneInfo;
import emu.lunarcore.server.packet.send.PacketSceneEntityUpdateScNotify;
import emu.lunarcore.server.packet.send.PacketSceneGroupRefreshScNotify;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import lombok.Getter;

@Getter
public class Scene {
    private final Player player;
    private final int planeId;
    private final int floorId;
    private final int entryId;
    
    private int lastEntityId = 0;

    // Avatar entites
    private IntSet avatarEntityIds;
    private Int2ObjectMap<GameAvatar> avatars;

    // Other entities TODO
    private Int2ObjectMap<GameEntity> entities;

    public Scene(Player player, int planeId, int floorId, int entryId) {
        this.player = player;
        this.planeId = planeId;
        this.floorId = floorId;
        this.entryId = entryId;

        // Setup avatars
        this.avatarEntityIds = new IntOpenHashSet();
        this.avatars = new Int2ObjectOpenHashMap<>();
        this.entities = new Int2ObjectOpenHashMap<>();

        PlayerLineup lineup = getPlayer().getLineupManager().getCurrentLineup();

        for (int avatarId : lineup.getAvatars()) {
            GameAvatar avatar = getPlayer().getAvatarById(avatarId);
            if (avatar == null) continue;

            this.avatars.put(avatarId, avatar);

            // Add entity id
            avatar.setEntityId(this.getNextEntityId());
            this.avatarEntityIds.add(avatar.getEntityId());
        }
        
        // Spawn monsters
        FloorInfo floorInfo = GameData.getFloorInfo(this.planeId, this.floorId);
        if (floorInfo == null) return;
        
        for (GroupInfo group : floorInfo.getGroups().values()) {
            // Skip non-server groups
            if (group.getLoadSide() != GroupLoadSide.Server) {
                continue;
            }
            
            // Add monsters
            if (group.getMonsterList() != null || group.getMonsterList().size() > 0) {
                for (MonsterInfo monsterInfo : group.getMonsterList()) {
                    // Get excels from game data
                    NpcMonsterExcel excel = GameData.getNpcMonsterExcelMap().get(monsterInfo.getNPCMonsterID());
                    StageExcel stage = GameData.getStageExcelMap().get(1);
                    if (excel == null || stage == null) continue;
                    
                    // Create monster with excels
                    EntityMonster monster = new EntityMonster(excel, stage, monsterInfo.clonePos());
                    monster.getRot().setY((int) (monsterInfo.getRotY() * 1000f));
                    monster.setInstId(monsterInfo.getID());
                    monster.setEventId(monsterInfo.getEventID());
                    monster.setGroupId(group.getId());
                    
                    // Add to monsters
                    this.addEntity(monster);
                }
            }
            
            // Add props
            if (group.getPropList() != null || group.getPropList().size() > 0) {
                for (PropInfo propInfo : group.getPropList()) {
                    // Dont add deleted props?
                    /*
                    if (propInfo.isIsDelete()) {
                        continue;
                    }
                    */
                    
                    // Create prop from prop info
                    EntityProp prop = new EntityProp(propInfo.getPropID(), propInfo.clonePos());
                    //prop.setState(propInfo.getState());
                    prop.getRot().set(
                            (int) (propInfo.getRotX() * 1000f),
                            (int) (propInfo.getRotY() * 1000f),
                            (int) (propInfo.getRotZ() * 1000f)
                    );
                    prop.setInstId(propInfo.getID());
                    prop.setGroupId(group.getId());
                    
                    // Add to monsters
                    this.addEntity(prop);
                }
            }
        }
    }

    private int getNextEntityId() {
        return ++lastEntityId;
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

    public synchronized void addEntity(GameEntity entity) {
        // Dont add if monster id already exists
        if (entity.getEntityId() != 0) return;
        // Set entity id and add monster to entity map
        entity.setEntityId(this.getNextEntityId());
        this.entities.put(entity.getEntityId(), entity);
    }
    
    public synchronized void removeEntity(GameEntity entity) {
        removeEntity(entity.getEntityId());
    }
    
    public synchronized void removeEntity(int entityId) {
        GameEntity entity = this.entities.remove(entityId);

        if (entity != null) {
            player.sendPacket(new PacketSceneGroupRefreshScNotify(null, entity));
        }
    }
    
    public SceneInfo toProto() {
        // Proto
        var proto = SceneInfo.newInstance()
                .setWorldId(301)
                .setLCMMECNPOBA(this.getPlaneId() == GameConstants.HOME_PLANE_ID ? 3 : 2)
                .setPlaneId(this.getPlaneId())
                .setFloorId(this.getFloorId())
                .setEntryId(this.getEntryId());

        // Get current lineup
        PlayerLineup lineup = getPlayer().getLineupManager().getCurrentLineup();
        int leaderAvatarId = lineup.getAvatars().get(getPlayer().getLineupManager().getCurrentLeader());

        // Scene group
        var playerGroup = SceneEntityGroupInfo.newInstance();

        for (var avatar : avatars.values()) {
            playerGroup.addEntityList(avatar.toSceneEntityProto());

            if (leaderAvatarId == avatar.getAvatarId()) {
                proto.setLeaderEntityId(avatar.getEntityId());
            }
        }

        proto.addEntityGroupList(playerGroup);

        // Sort entities into groups
        var groups = new Int2ObjectOpenHashMap<SceneEntityGroupInfo>();

        for (var monster : entities.values()) {
            var group = groups.computeIfAbsent(monster.getGroupId(), i -> SceneEntityGroupInfo.newInstance().setGroupId(i));
            group.addEntityList(monster.toSceneEntityProto());
        }

        for (var group : groups.values()) {
            proto.addEntityGroupList(group);
        }

        // Done
        return proto;
    }
}
