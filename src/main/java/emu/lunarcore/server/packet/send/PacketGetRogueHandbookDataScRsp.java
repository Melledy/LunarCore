package emu.lunarcore.server.packet.send;

import emu.lunarcore.proto.GetRogueHandbookDataScRspOuterClass.GetRogueHandbookDataScRsp;
import emu.lunarcore.proto.RogueHandbookDataOuterClass.RogueHandbookData;
import emu.lunarcore.server.packet.BasePacket;
import emu.lunarcore.server.packet.CmdId;

public class PacketGetRogueHandbookDataScRsp extends BasePacket {

    public PacketGetRogueHandbookDataScRsp() {
        super(CmdId.GetRogueHandbookDataScRsp);
        
        /*
        var handbook = RogueHandbookData.newInstance()
                .setRogueCurrentVersion(1)
                .setIsMiracleUnlock(true);
        */
        
        var data = GetRogueHandbookDataScRsp.newInstance();
        data.getMutableHandbookInfo();
        
        this.setData(data);
    }
}
