package emu.lunarcore.server.packet.send;

import emu.lunarcore.data.excel.TutorialGuideExcel;
import emu.lunarcore.proto.FinishTutorialGuideScRspOuterClass.FinishTutorialGuideScRsp;
import emu.lunarcore.proto.TutorialGuideOuterClass.TutorialGuide;
import emu.lunarcore.proto.TutorialStatusOuterClass.TutorialStatus;
import emu.lunarcore.server.packet.BasePacket;
import emu.lunarcore.server.packet.CmdId;

public class PacketFinishTutorialGuideScRsp extends BasePacket {

    public PacketFinishTutorialGuideScRsp(TutorialGuideExcel excel) {
        super(CmdId.FinishTutorialGuideScRsp);

        var data = FinishTutorialGuideScRsp.newInstance();
        
        if (excel == null) {
            data.setRetcode(1);
        } else {
            // Set reward
            data.getMutableReward();
            
            // Set tutorial guide
            var tutorial = TutorialGuide.newInstance()
                    .setId(excel.getId())
                    .setStatus(TutorialStatus.TUTORIAL_FINISH);
            
            data.setTutorialGuide(tutorial);
        }
        
        this.setData(data);
    }
}
