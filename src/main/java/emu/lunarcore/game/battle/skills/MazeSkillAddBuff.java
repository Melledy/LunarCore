package emu.lunarcore.game.battle.skills;

import java.util.List;

import emu.lunarcore.game.avatar.GameAvatar;
import emu.lunarcore.game.scene.SceneBuff;
import emu.lunarcore.game.scene.entity.EntityMonster;
import emu.lunarcore.game.scene.entity.GameEntity;
import emu.lunarcore.proto.MotionInfoOuterClass.MotionInfo;
import emu.lunarcore.server.packet.send.PacketSyncEntityBuffChangeListScNotify;
import lombok.Getter;
import lombok.Setter;

@Getter
public class MazeSkillAddBuff extends MazeSkillAction {
    private int buffId;
    private int duration;
    
    @Setter
    private boolean sendBuffPacket;
    
    public MazeSkillAddBuff(int buffId, int duration) {
        this.buffId = buffId;
        this.duration = duration;
    }
    
    @Override
    public void onCast(GameAvatar caster, MotionInfo castPosition) {
        caster.addBuff(buffId, duration);
    }
    
    @Override
    public void onCastHit(GameAvatar caster, List<? extends GameEntity> entities) {
        for (GameEntity entity : entities) {
            if (entity instanceof EntityMonster monster) {
                // Add buff to monster
                var buff = monster.addBuff(caster.getAvatarId(), buffId, duration);
                
                // Send packet
                if (buff != null && this.sendBuffPacket) {
                    caster.getOwner().sendPacket(new PacketSyncEntityBuffChangeListScNotify(entity.getEntityId(), buff));
                }
            }
        }
    }
    
    @Override
    public void onAttack(GameAvatar caster, List<? extends GameEntity> targets) {
        // Add debuff to monsters
        for (GameEntity target : targets) {
            if (target instanceof EntityMonster monster) {
                // Set as temp buff
                monster.setTempBuff(new SceneBuff(caster.getAvatarId(), buffId));
            }
        }
    }
}
