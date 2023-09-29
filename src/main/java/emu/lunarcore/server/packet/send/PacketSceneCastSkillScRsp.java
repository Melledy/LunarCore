package emu.lunarcore.server.packet.send;

import emu.lunarcore.game.battle.Battle;
import emu.lunarcore.proto.SceneCastSkillScRspOuterClass.SceneCastSkillScRsp;
import emu.lunarcore.server.packet.BasePacket;
import emu.lunarcore.server.packet.CmdId;

public class PacketSceneCastSkillScRsp extends BasePacket {

    public PacketSceneCastSkillScRsp() {
        this(0);
    }
    
    public PacketSceneCastSkillScRsp(int retcode) {
        super(CmdId.SceneCastSkillScRsp);

        var data = SceneCastSkillScRsp.newInstance()
                .setRetcode(retcode);

        this.setData(data);
    }

    public PacketSceneCastSkillScRsp(Battle battle) {
        super(CmdId.SceneCastSkillScRsp);

        // Build data
        var data = SceneCastSkillScRsp.newInstance()
                .setBattleInfo(battle.toProto());

        this.setData(data);
    }
}
