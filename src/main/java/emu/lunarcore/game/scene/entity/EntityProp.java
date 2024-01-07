package emu.lunarcore.game.scene.entity;

import emu.lunarcore.data.config.GroupInfo;
import emu.lunarcore.data.config.PropInfo;
import emu.lunarcore.data.excel.PropExcel;
import emu.lunarcore.game.enums.PropState;
import emu.lunarcore.game.enums.PropType;
import emu.lunarcore.game.scene.Scene;
import emu.lunarcore.game.scene.entity.extra.PropRogueData;
import emu.lunarcore.proto.MotionInfoOuterClass.MotionInfo;
import emu.lunarcore.proto.SceneEntityInfoOuterClass.SceneEntityInfo;
import emu.lunarcore.proto.ScenePropInfoOuterClass.ScenePropInfo;
import emu.lunarcore.server.packet.send.PacketSceneGroupRefreshScNotify;
import emu.lunarcore.util.Position;
import lombok.Getter;
import lombok.Setter;

@Getter
public class EntityProp implements GameEntity {
    @Setter private PropInfo propInfo;
    
    @Setter private int entityId;
    @Setter private int groupId;
    @Setter private int instId;
    private PropState state;
    
    private final Scene scene;
    private final PropExcel excel;
    private final Position pos;
    private final Position rot;

    // Prop extra info
    @Setter private PropRogueData rogueData;
    
    public EntityProp(Scene scene, PropExcel excel, GroupInfo group, PropInfo propInfo) {
        this.scene = scene;
        this.excel = excel;
        this.pos = new Position();
        this.rot = new Position();
        this.state = PropState.Closed;
        
        if (propInfo != null) {
            this.propInfo = propInfo;
            this.instId = propInfo.getID();
            this.getPos().set(propInfo.getPos());
            this.getRot().set(propInfo.getRot());
        }
        
        if (group != null) {
            this.groupId = group.getId();
        }
    }
    
    public int getPropId() {
        return excel.getId();
    }
    
    public PropType getPropType() {
        return getExcel().getPropType();
    }
    
    public boolean setState(PropState state) {
        return this.setState(state, this.getScene().isLoaded());
    }
    
    public boolean setState(PropState state, boolean sendPacket) {
        // Only set state if its been changed
        PropState oldState = this.getState();
        if (oldState == state) return false;
        
        // Sanity check
        if (!this.getExcel().getPropStateList().contains(state)) {
            return false;
        }
        
        // Set state
        this.state = state;
        
        // Sync state update to client
        if (sendPacket) {
            this.getScene().getPlayer().sendPacket(new PacketSceneGroupRefreshScNotify(this, null));
        }
        
        // Success
        return true;
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
        
        if (this.rogueData != null) {
            prop.setExtraInfo(this.rogueData.toProto());
        }

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
        return "[Prop] EntityId: " + this.getEntityId() + 
                ", PropId: " + this.getExcel().getId() + 
                " (" + this.getExcel().getPropType() + ")" + 
                ", Group: " + this.groupId + 
                ", ConfigId: " + this.getInstId();
    }
}
