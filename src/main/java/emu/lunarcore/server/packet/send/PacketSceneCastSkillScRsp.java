package emu.lunarcore.server.packet.send;

import emu.lunarcore.game.battle.Battle;
import emu.lunarcore.proto.SceneCastSkillScRspOuterClass.SceneCastSkillScRsp;
import emu.lunarcore.server.packet.BasePacket;
import emu.lunarcore.server.packet.CmdId;

public class PacketSceneCastSkillScRsp extends BasePacket {

    /**
     * Returns an error to the client
     */
    public PacketSceneCastSkillScRsp() {
        super(CmdId.SceneCastSkillScRsp);

        var data = SceneCastSkillScRsp.newInstance()
                .setRetcode(1);

        this.setData(data);
    }
    
    /**
     * No battle was started, but we still want to send the attacked group id to the client
     * @param attackedGroupId
     */
    public PacketSceneCastSkillScRsp(int attackedGroupId) {
        super(CmdId.SceneCastSkillScRsp);

        var data = SceneCastSkillScRsp.newInstance()
                .setAttackedGroupId(attackedGroupId);

        this.setData(data);
    }

    public PacketSceneCastSkillScRsp(Battle battle, int attackedGroupId) {
        super(CmdId.SceneCastSkillScRsp);

        // Build data
        var data = SceneCastSkillScRsp.newInstance()
                .setAttackedGroupId(attackedGroupId)
                .setBattleInfo(battle.toProto());

        this.setData(data);
    }
}
