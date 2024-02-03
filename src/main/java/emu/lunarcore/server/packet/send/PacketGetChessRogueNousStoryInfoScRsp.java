package emu.lunarcore.server.packet.send;

import emu.lunarcore.data.GameData;
import emu.lunarcore.proto.ChessRogueNousMainStoryInfoOuterClass.ChessRogueNousMainStoryInfo;
import emu.lunarcore.proto.ChessRogueNousSubStoryInfoOuterClass.ChessRogueNousSubStoryInfo;
import emu.lunarcore.proto.GetChessRogueNousStoryInfoScRspOuterClass.GetChessRogueNousStoryInfoScRsp;
import emu.lunarcore.server.packet.BasePacket;
import emu.lunarcore.server.packet.CmdId;

public class PacketGetChessRogueNousStoryInfoScRsp extends BasePacket {
    public PacketGetChessRogueNousStoryInfoScRsp() {
        super(CmdId.GetChessRogueNousStoryInfoScRsp);
        
        var proto = GetChessRogueNousStoryInfoScRsp.newInstance();

        for (var entry : GameData.getRogueNousMainStoryExcelMap().keySet()) {
            proto.addMainStoryInfo(ChessRogueNousMainStoryInfo.newInstance()
                    .setStoryId(entry)
                    .setStatus(2));
        }

        for (var entry : GameData.getRogueNousSubStoryExcelMap().keySet()) {
            proto.addSubStoryInfo(ChessRogueNousSubStoryInfo.newInstance()
                    .setSubStoryId(entry));
        }
        
        this.setData(proto);
    }
}
