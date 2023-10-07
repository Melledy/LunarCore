package emu.lunarcore.game.scene.entity;

import emu.lunarcore.data.excel.NpcMonsterExcel;
import emu.lunarcore.data.excel.StageExcel;
import emu.lunarcore.game.scene.Scene;
import emu.lunarcore.game.scene.triggers.PropTriggerType;
import emu.lunarcore.proto.MotionInfoOuterClass.MotionInfo;
import emu.lunarcore.proto.SceneEntityInfoOuterClass.SceneEntityInfo;
import emu.lunarcore.proto.SceneNpcMonsterInfoOuterClass.SceneNpcMonsterInfo;
import emu.lunarcore.proto.VectorOuterClass.Vector;
import emu.lunarcore.util.Position;
import lombok.Getter;
import lombok.Setter;

@Getter
public class EntityMonster implements GameEntity {
    @Setter private int entityId;
    @Setter private int worldLevel;
    @Setter private int groupId;
    @Setter private int instId;
    @Setter private int eventId;
    @Setter private int overrideStageId;
    
    private final Scene scene;
    private final NpcMonsterExcel excel;
    private final Position pos;
    private final Position rot;
    
    public EntityMonster(Scene scene, NpcMonsterExcel excel, Position pos) {
        this.scene = scene;
        this.excel = excel;
        this.pos = pos;
        this.rot = new Position();
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
        getScene().fireTrigger(PropTriggerType.MONSTER_DIE, this.getGroupId());
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
