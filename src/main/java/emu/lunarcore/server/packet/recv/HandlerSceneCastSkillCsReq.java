package emu.lunarcore.server.packet.recv;

import emu.lunarcore.game.avatar.GameAvatar;
import emu.lunarcore.game.battle.skills.MazeSkill;
import emu.lunarcore.game.player.Player;
import emu.lunarcore.proto.SceneCastSkillCsReqOuterClass.SceneCastSkillCsReq;
import emu.lunarcore.server.game.GameSession;
import emu.lunarcore.server.packet.CmdId;
import emu.lunarcore.server.packet.Opcodes;
import emu.lunarcore.server.packet.PacketHandler;
import emu.lunarcore.server.packet.send.PacketSceneCastSkillMpUpdateScNotify;
import emu.lunarcore.server.packet.send.PacketSceneCastSkillScRsp;
import it.unimi.dsi.fastutil.ints.IntLinkedOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;

@Opcodes(CmdId.SceneCastSkillCsReq)
public class HandlerSceneCastSkillCsReq extends PacketHandler {
    
    @Override
    public void handle(GameSession session, byte[] data) throws Exception {
        var req = SceneCastSkillCsReq.parseFrom(data);
        
        // Setup variables
        Player player = session.getPlayer();
        MazeSkill skill = null;
        
        // Check if player casted a maze skill
        if (player.getScene().getAvatarEntityIds().contains(req.getCasterId())) {
            // Get casting avatar
            GameAvatar caster = player.getCurrentLeaderAvatar();
            
            // Sanity check, but should never happen
            if (caster == null) {
                session.send(new PacketSceneCastSkillScRsp(req.getAttackedGroupId()));
                return;
            }
            
            // Check if normal attack or technique was used
            if (req.getSkillIndex() > 0) {
                // Spend one skill point
                player.getCurrentLineup().removeMp(1);
                session.send(new PacketSceneCastSkillMpUpdateScNotify(req.getAttackedGroupId(), player.getCurrentLineup().getMp()));
                // Cast skill effects
                if (caster.getExcel().getMazeSkill() != null) {
                    skill = caster.getExcel().getMazeSkill();
                    skill.onCast(caster, req.getTargetMotion());
                }
            } else {
                skill = caster.getExcel().getMazeAttack();
            }
        }
        
        if (req.hasHitTargetEntityIdList()) {
            // Parse targets efficiently (skips integer boxing)
            IntSet hitTargets = new IntLinkedOpenHashSet();
            for (int i = 0; i < req.getHitTargetEntityIdList().length(); i++) {
                hitTargets.add(req.getHitTargetEntityIdList().get(i));
            }
            
            IntSet assistMonsters = new IntLinkedOpenHashSet();
            for (int i = 0; i < req.getAssistMonsterEntityIdList().length(); i++) {
                assistMonsters.add(req.getAssistMonsterEntityIdList().get(i));
            }
            
            // Start battle
            session.getServer().getBattleService().startBattle(player, req.getCasterId(), req.getAttackedGroupId(), skill, hitTargets, assistMonsters);
        } else {
            // We had no targets for some reason
            session.send(new PacketSceneCastSkillScRsp(req.getAttackedGroupId()));
        }
    }

}
