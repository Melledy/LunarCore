package emu.lunarcore.server.packet.send;

import emu.lunarcore.proto.SceneCastSkillCostMpScRspOuterClass.SceneCastSkillCostMpScRsp;
import emu.lunarcore.server.packet.BasePacket;
import emu.lunarcore.server.packet.CmdId;

public class PacketSceneCastSkillCostMpScRsp extends BasePacket {

    public PacketSceneCastSkillCostMpScRsp(int attackedGroupId) {
        super(CmdId.SceneCastSkillCostMpScRsp);

        var data = SceneCastSkillCostMpScRsp.newInstance()
                .setAttackedGroupId(attackedGroupId);

        this.setData(data);
    }
}
