package emu.lunarcore.server.packet.send;

import emu.lunarcore.game.trial.TrialInstance;
import emu.lunarcore.proto.StartTrialActivityScRspOuterClass.StartTrialActivityScRsp;
import emu.lunarcore.server.packet.BasePacket;
import emu.lunarcore.server.packet.CmdId;

public class PacketStartTrialActivityScRsp extends BasePacket {

    public PacketStartTrialActivityScRsp(TrialInstance trial) {
        super(CmdId.StartTrialActivityScRsp);

        var proto = StartTrialActivityScRsp.newInstance();

        if (trial != null) {
            proto.setStageId(trial.getStageId());
        } else {
            proto.setRetcode(1);
        }

        this.setData(proto);
    }
}
