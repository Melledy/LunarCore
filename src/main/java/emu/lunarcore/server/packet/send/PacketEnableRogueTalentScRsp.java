package emu.lunarcore.server.packet.send;

import emu.lunarcore.game.rogue.RogueManager;
import emu.lunarcore.proto.EnableRogueTalentScRspOuterClass.EnableRogueTalentScRsp;
import emu.lunarcore.server.packet.BasePacket;
import emu.lunarcore.server.packet.CmdId;

public class PacketEnableRogueTalentScRsp extends BasePacket {

    public PacketEnableRogueTalentScRsp() {
        super(CmdId.EnableRogueTalentScRsp);
        
        var data = EnableRogueTalentScRsp.newInstance()
                .setRetcode(1);
        
        this.setData(data);
    }
    
    public PacketEnableRogueTalentScRsp(RogueManager rogueManager) {
        super(CmdId.EnableRogueTalentScRsp);
        
        var data = EnableRogueTalentScRsp.newInstance()
                .setTalentInfo(rogueManager.toTalentInfoProto());
        
        this.setData(data);
    }
    
}