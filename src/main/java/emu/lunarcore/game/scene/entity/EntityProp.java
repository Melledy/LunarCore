package emu.lunarcore.game.scene.entity;

import emu.lunarcore.data.excel.PropExcel;
import emu.lunarcore.game.enums.PropState;
import emu.lunarcore.game.scene.Scene;
import emu.lunarcore.proto.MotionInfoOuterClass.MotionInfo;
import emu.lunarcore.proto.SceneEntityInfoOuterClass.SceneEntityInfo;
import emu.lunarcore.proto.ScenePropInfoOuterClass.ScenePropInfo;
import emu.lunarcore.util.Position;
import lombok.Getter;
import lombok.Setter;

@Getter
public class EntityProp implements GameEntity {
    @Setter private int entityId;
    @Setter private int groupId;
    @Setter private int instId;
    @Setter private PropState state;
    
    private PropExcel excel;
    private Position pos;
    private Position rot;
    
    public EntityProp(PropExcel excel, Position pos) {
        this.excel = excel;
        this.pos = pos;
        this.rot = new Position();
        this.state = PropState.Closed;
    }
    
    public int getPropId() {
        return excel.getId();
    }
    
    @Override
    public void onRemove(Scene scene) {
        if (excel.isRecoverMp()) {
            scene.getPlayer().getLineupManager().addMp(2);
        } else if (excel.isRecoverHp()) {
            scene.getPlayer().getCurrentLineup().heal(2500);
        }
    }

    @Override
    public SceneEntityInfo toSceneEntityProto() {
        var prop = ScenePropInfo.newInstance()
                .setPropId(this.getPropId())
                .setPropState(this.getState().getVal());

        var proto = SceneEntityInfo.newInstance()
                .setEntityId(this.getEntityId())
                .setGroupId(this.getGroupId())
                .setInstId(this.getInstId())
                .setMotion(MotionInfo.newInstance().setPos(getPos().toProto()).setRot(getRot().toProto()))
                .setProp(prop);

        return proto;
    }

}
