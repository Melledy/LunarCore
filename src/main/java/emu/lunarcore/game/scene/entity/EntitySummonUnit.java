package emu.lunarcore.game.scene.entity;

import emu.lunarcore.LunarCore;
import emu.lunarcore.data.excel.SummonUnitExcel;
import emu.lunarcore.game.avatar.GameAvatar;
import emu.lunarcore.game.scene.Scene;
import emu.lunarcore.proto.MotionInfoOuterClass.MotionInfo;
import emu.lunarcore.proto.SceneEntityInfoOuterClass.SceneEntityInfo;
import emu.lunarcore.proto.SceneSummonUnitInfoOuterClass.SceneSummonUnitInfo;
import emu.lunarcore.util.Position;
import lombok.Getter;
import lombok.Setter;

@Getter
public class EntitySummonUnit implements GameEntity {
    @Setter private int entityId;
    private final GameAvatar caster;
    private final SummonUnitExcel excel;
    
    private final Scene scene;
    private final Position pos;
    private final Position rot;
    private final long createTime;
    
    private int attachedEntityId;
    private int duration;
    private long expiry;
    
    public EntitySummonUnit(Scene scene, GameAvatar caster, SummonUnitExcel excel, Position pos, Position rot) {
        this.scene = scene;
        this.caster = caster;
        this.excel = excel;
        this.pos = pos;
        this.rot = rot;
        this.createTime = System.currentTimeMillis();
        
        // Attach summon unit to an entity
        String attachPoint = excel.getInfo().getAttachPoint();
        if (attachPoint != null && attachPoint.equals("Origin")) {
            this.attachedEntityId = caster.getEntityId();
        }
    }
    
    public void setDuration(int seconds) {
        this.duration = seconds * 1000;
        this.expiry = this.createTime + duration;
    }
    
    public boolean isExpired() {
        return System.currentTimeMillis() > this.expiry;
    }
    
    @Override
    public SceneEntityInfo toSceneEntityProto() {
        var summon = SceneSummonUnitInfo.newInstance()
                .setLifeTimeMs(this.getDuration())
                .setCreateTimeMs(LunarCore.convertToServerTime(this.getCreateTime()))
                .setCasterEntityId(this.getCaster().getEntityId())
                .setAttachEntityId(this.getAttachedEntityId())
                .setSummonUnitId(this.getExcel().getId());
        
        for (var trigger : this.getExcel().getInfo().getCustomTriggers()) {
            summon.addTriggerNameList(trigger.getTriggerName());
        }

        var proto = SceneEntityInfo.newInstance()
                .setEntityId(this.getEntityId())
                .setMotion(MotionInfo.newInstance().setPos(getPos().toProto()).setRot(getRot().toProto()))
                .setSummonUnit(summon);

        return proto;
    }
}
