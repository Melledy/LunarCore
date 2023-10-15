package emu.lunarcore.game.scene.entity;

import emu.lunarcore.data.config.PropInfo;
import emu.lunarcore.data.excel.PropExcel;
import emu.lunarcore.game.enums.PropState;
import emu.lunarcore.game.scene.Scene;
import emu.lunarcore.proto.MotionInfoOuterClass.MotionInfo;
import emu.lunarcore.proto.SceneEntityInfoOuterClass.SceneEntityInfo;
import emu.lunarcore.proto.ScenePropInfoOuterClass.ScenePropInfo;
import emu.lunarcore.server.packet.send.PacketSceneGroupRefreshScNotify;
import emu.lunarcore.util.Position;
import lombok.Getter;
import lombok.Setter;

@Getter
public class EntityProp implements GameEntity {
    @Setter private int entityId;
    @Setter private int groupId;
    @Setter private int instId;
    private PropState state;
    
    private final Scene scene;
    private final PropExcel excel;
    private final Position pos;
    private final Position rot;
    
    @Setter
    private PropInfo propInfo;
    
    public EntityProp(Scene scene, PropExcel excel, Position pos) {
        this.scene = scene;
        this.excel = excel;
        this.pos = pos;
        this.rot = new Position();
        this.state = PropState.Closed;
    }
    
    public int getPropId() {
        return excel.getId();
    }
    
    public void setState(PropState state) {
        // Set state
        this.state = state;
        // Sync state update to client
        if (this.getScene().isLoaded()) {
            this.getScene().getPlayer().sendPacket(new PacketSceneGroupRefreshScNotify(this, null));
        }
    }
    
    @Override
    public void onRemove() {
        if (excel.isRecoverMp()) {
            scene.getPlayer().getCurrentLineup().addMp(2);
        } else if (excel.isRecoverHp()) {
            scene.getPlayer().getCurrentLineup().heal(2500, false);
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

    @Override
    public String toString() {
        return "Prop: " + this.getEntityId() + ", Group: " + this.groupId + ", Inst: " + this.getInstId();
    }
}
