package emu.lunarcore.game.scene.entity;

import emu.lunarcore.data.config.GroupInfo;
import emu.lunarcore.data.config.MonsterInfo;
import emu.lunarcore.data.excel.NpcMonsterExcel;
import emu.lunarcore.game.scene.Scene;
import emu.lunarcore.game.scene.triggers.PropTriggerType;
import emu.lunarcore.proto.MotionInfoOuterClass.MotionInfo;
import emu.lunarcore.proto.SceneEntityInfoOuterClass.SceneEntityInfo;
import emu.lunarcore.proto.SceneNpcMonsterInfoOuterClass.SceneNpcMonsterInfo;
import emu.lunarcore.util.Position;
import lombok.Getter;
import lombok.Setter;

@Getter
public class EntityMonster implements GameEntity {
    @Setter private NpcMonsterExcel excel;
    @Setter private int entityId;
    @Setter private int worldLevel;
    @Setter private int groupId;
    @Setter private int instId;
    @Setter private int eventId;
    
    private final Scene scene;
    private final Position pos;
    private final Position rot;
    
    private int farmElementId;
    @Setter private int overrideStageId;
    @Setter private int overrideLevel;
    
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
        if (this.overrideStageId == 0) {
            return (this.getEventId() * 10) + worldLevel;
        } else {
            return this.overrideStageId;
        }
    }
    
    @Override
    public void onRemove() {
        // Try to fire any triggers
        getScene().invokeTrigger(PropTriggerType.MONSTER_DIE, this.getGroupId(), this.getInstId());
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
