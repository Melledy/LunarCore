package emu.lunarcore.server.packet.recv;

import emu.lunarcore.game.avatar.GameAvatar;
import emu.lunarcore.game.battle.skills.MazeSkill;
import emu.lunarcore.proto.SceneCastSkillCsReqOuterClass.SceneCastSkillCsReq;
import emu.lunarcore.server.game.GameSession;
import emu.lunarcore.server.packet.CmdId;
import emu.lunarcore.server.packet.Opcodes;
import emu.lunarcore.server.packet.PacketHandler;
import emu.lunarcore.server.packet.send.PacketSceneCastSkillMpUpdateScNotify;
import emu.lunarcore.server.packet.send.PacketSceneCastSkillScRsp;

@Opcodes(CmdId.SceneCastSkillCsReq)
public class HandlerSceneCastSkillCsReq extends PacketHandler {

    @Override
    public void handle(GameSession session, byte[] header, byte[] data) throws Exception {
        var req = SceneCastSkillCsReq.parseFrom(data);
        
        MazeSkill castedSkill = null;
        
        // Check if player casted a maze skill
        if (req.getSkillIndex() > 0 && session.getPlayer().getScene().getAvatarEntityIds().contains(req.getAttackerId())) {
            // Spend one skill point
            session.getPlayer().getLineupManager().removeMp(1);
            session.send(new PacketSceneCastSkillMpUpdateScNotify(req.getAttackedGroupId(), session.getPlayer().getLineupManager().getMp()));
            // Cast skill effects
            GameAvatar caster = session.getPlayer().getLineupManager().getCurrentLeaderAvatar();
            if (caster != null && caster.getExcel().getMazeSkill() != null) {
                castedSkill = caster.getExcel().getMazeSkill();
                castedSkill.onCast(caster, req.getTargetMotion());
            }
        }
        
        if (req.hasAttackedEntityIdList()) {
            session.getServer().getBattleService().startBattle(session.getPlayer(), req.getAttackerId(), castedSkill, req.getAttackedEntityIdList());
        } else {
            session.send(new PacketSceneCastSkillScRsp());
        }
    }

}
