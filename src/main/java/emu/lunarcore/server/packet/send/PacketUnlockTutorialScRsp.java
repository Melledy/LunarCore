package emu.lunarcore.server.packet.send;

import emu.lunarcore.proto.TutorialOuterClass.Tutorial;
import emu.lunarcore.proto.TutorialStatusOuterClass.TutorialStatus;
import emu.lunarcore.proto.UnlockTutorialScRspOuterClass.UnlockTutorialScRsp;
import emu.lunarcore.server.packet.BasePacket;
import emu.lunarcore.server.packet.CmdId;

public class PacketUnlockTutorialScRsp extends BasePacket {

    public PacketUnlockTutorialScRsp(int id) {
        super(CmdId.UnlockTutorialScRsp);

        var data = UnlockTutorialScRsp.newInstance()
                .setTutorial(Tutorial.newInstance()
                        .setId(id)
                        .setStatus(TutorialStatus.TUTORIAL_UNLOCK)
                        );

        this.setData(data);
    }
}