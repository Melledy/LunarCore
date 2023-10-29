package emu.lunarcore.game.scene.entity;

import emu.lunarcore.game.scene.Scene;
import emu.lunarcore.proto.MotionInfoOuterClass.MotionInfo;
import emu.lunarcore.proto.NpcRogueInfoOuterClass.NpcRogueInfo;
import emu.lunarcore.proto.SceneEntityInfoOuterClass.SceneEntityInfo;
import emu.lunarcore.proto.SceneNpcInfoOuterClass.SceneNpcInfo;
import emu.lunarcore.util.Position;
import lombok.Getter;
import lombok.Setter;

@Getter
public class EntityNpc implements GameEntity {
    @Setter private int entityId;
    @Setter private int groupId;
    @Setter private int instId;
    @Setter private int npcId;
    
    private final Scene scene;
    private final Position pos;
    private final Position rot;
    
    @Setter private int rogueNpcId; 
    
    public EntityNpc(Scene scene, int npcId, Position pos) {
        this.scene = scene;
        this.npcId = npcId;
        this.pos = pos;
        this.rot = new Position();
    }

    @Override
    public SceneEntityInfo toSceneEntityProto() {
        // Base npc info
        var npc = SceneNpcInfo.newInstance()
                .setNpcId(this.getNpcId());
        
        // Rogue data
        if (this.rogueNpcId > 0) {
            var rogue = NpcRogueInfo.newInstance()
                    .setRogueNpcId(this.rogueNpcId);
            
            npc.getMutableExtraInfo().setRogueInfo(rogue);
        }

        // Main entity proto
        var proto = SceneEntityInfo.newInstance()
                .setEntityId(this.getEntityId())
                .setGroupId(this.getGroupId())
                .setInstId(this.getInstId())
                .setMotion(MotionInfo.newInstance().setPos(getPos().toProto()).setRot(getRot().toProto()))
                .setNpc(npc);

        return proto;
    }

}
