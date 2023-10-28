package emu.lunarcore.server.packet.send;

import emu.lunarcore.game.rogue.RogueManager;
import emu.lunarcore.proto.GetRogueTalentInfoScRspOuterClass.GetRogueTalentInfoScRsp;
import emu.lunarcore.server.packet.BasePacket;
import emu.lunarcore.server.packet.CmdId;

public class PacketGetRogueTalentInfoScRsp extends BasePacket {

    public PacketGetRogueTalentInfoScRsp(RogueManager rogueManager) {
        super(CmdId.GetRogueTalentInfoScRsp);
        
        var data = GetRogueTalentInfoScRsp.newInstance()
                .setTalentInfo(rogueManager.toTalentInfoProto());
        
        this.setData(data);
    }
}
