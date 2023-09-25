package emu.lunarcore.game.scene;

import emu.lunarcore.data.excel.NpcMonsterExcel;
import emu.lunarcore.data.excel.StageExcel;
import emu.lunarcore.proto.MotionInfoOuterClass.MotionInfo;
import emu.lunarcore.proto.SceneEntityInfoOuterClass.SceneEntityInfo;
import emu.lunarcore.proto.SceneNpcMonsterInfoOuterClass.SceneNpcMonsterInfo;
import emu.lunarcore.proto.ScenePropInfoOuterClass.ScenePropInfo;
import emu.lunarcore.proto.VectorOuterClass.Vector;
import emu.lunarcore.util.Position;
import lombok.Getter;
import lombok.Setter;

@Getter
public class EntityProp implements GameEntity {
    @Setter private int entityId;
    @Setter private int groupId;
    @Setter private int instId;
    @Setter private int propId;
    @Setter private PropState state;
    
    private Position pos;
    private Position rot;
    
    public EntityProp(int propId, Position pos) {
        this.propId = propId;
        this.pos = pos;
        this.rot = new Position();
        this.state = PropState.Closed;
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
