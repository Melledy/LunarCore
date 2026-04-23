package emu.lunarcore.server.packet.send;

import emu.lunarcore.data.GameData;
import emu.lunarcore.proto.GetTutorialScRspOuterClass.GetTutorialScRsp;
import emu.lunarcore.proto.TutorialOuterClass.Tutorial;
import emu.lunarcore.proto.TutorialStatusOuterClass.TutorialStatus;
import emu.lunarcore.server.packet.BasePacket;
import emu.lunarcore.server.packet.CmdId;

public class PacketGetTutorialScRsp extends BasePacket {

    public PacketGetTutorialScRsp() {
        super(CmdId.GetTutorialScRsp);

        var data = GetTutorialScRsp.newInstance();

        for (var excel : GameData.getTutorialExcelMap()) {
            var tutorialGuide = Tutorial.newInstance()
                    .setId(excel.getId())
                    .setStatus(TutorialStatus.TUTORIAL_FINISH);

            data.addTutorialList(tutorialGuide);
        }

        this.setData(data);
    }
}
