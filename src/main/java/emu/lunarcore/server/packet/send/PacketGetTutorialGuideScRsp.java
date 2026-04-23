package emu.lunarcore.server.packet.send;

import emu.lunarcore.data.GameData;
import emu.lunarcore.proto.GetTutorialGuideScRspOuterClass.GetTutorialGuideScRsp;
import emu.lunarcore.proto.TutorialGuideOuterClass.TutorialGuide;
import emu.lunarcore.proto.TutorialStatusOuterClass.TutorialStatus;
import emu.lunarcore.server.packet.BasePacket;
import emu.lunarcore.server.packet.CmdId;

public class PacketGetTutorialGuideScRsp extends BasePacket {

    public PacketGetTutorialGuideScRsp() {
        super(CmdId.GetTutorialGuideScRsp);

        var data = GetTutorialGuideScRsp.newInstance();

        for (var excel : GameData.getTutorialGuideExcelMap()) {
            var tutorial = TutorialGuide.newInstance()
                    .setId(excel.getId())
                    .setStatus(TutorialStatus.TUTORIAL_FINISH);

            data.addTutorialGuideList(tutorial);
        }

        this.setData(data);
    }
}
