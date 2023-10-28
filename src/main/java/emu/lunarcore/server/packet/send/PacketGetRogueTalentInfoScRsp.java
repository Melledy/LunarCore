package emu.lunarcore.server.packet.send;

import emu.lunarcore.data.GameData;
import emu.lunarcore.data.excel.RogueTalentExcel;
import emu.lunarcore.game.rogue.RogueManager;
import emu.lunarcore.proto.GetRogueTalentInfoScRspOuterClass.GetRogueTalentInfoScRsp;
import emu.lunarcore.proto.RogueTalentOuterClass.RogueTalent;
import emu.lunarcore.proto.RogueTalentStatusOuterClass.RogueTalentStatus;
import emu.lunarcore.server.packet.BasePacket;
import emu.lunarcore.server.packet.CmdId;

public class PacketGetRogueTalentInfoScRsp extends BasePacket {

    public PacketGetRogueTalentInfoScRsp(RogueManager rogueManager) {
        super(CmdId.GetRogueTalentInfoScRsp);
        
        var data = GetRogueTalentInfoScRsp.newInstance();
        
        for (RogueTalentExcel excel : GameData.getRogueTalentExcelMap().values()) {
            var talent = RogueTalent.newInstance()
                    .setTalentId(excel.getTalentID())
                    .setStatus(RogueTalentStatus.ROGUE_TALENT_STATUS_UNLOCK);
            
            data.getMutableTalentInfo().addRogueTalent(talent);
        }
        
        this.setData(data);
    }
}
