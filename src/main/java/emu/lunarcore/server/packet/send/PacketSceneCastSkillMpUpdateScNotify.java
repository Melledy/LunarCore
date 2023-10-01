package emu.lunarcore.server.packet.send;

import emu.lunarcore.proto.SceneCastSkillMpUpdateScNotifyOuterClass.SceneCastSkillMpUpdateScNotify;
import emu.lunarcore.server.packet.BasePacket;
import emu.lunarcore.server.packet.CmdId;

public class PacketSceneCastSkillMpUpdateScNotify extends BasePacket {

    public PacketSceneCastSkillMpUpdateScNotify(int attackedGroupId, int mp) {
        super(CmdId.SceneCastSkillMpUpdateScNotify);
        
        var data = SceneCastSkillMpUpdateScNotify.newInstance()
                .setAttackedGroupId(attackedGroupId)
                .setMp(mp);
        
        this.setData(data);
    }
}
