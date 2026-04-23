package emu.lunarcore.server.packet.send;

import emu.lunarcore.game.trial.TrialInstance;
import emu.lunarcore.proto.CurTrialActivityScNotifyOuterClass.CurTrialActivityScNotify;
import emu.lunarcore.proto.TrialActivityStatusOuterClass.TrialActivityStatus;
import emu.lunarcore.server.packet.BasePacket;
import emu.lunarcore.server.packet.CmdId;

public class PacketCurTrialActivityScNotify extends BasePacket {

    public PacketCurTrialActivityScNotify(TrialInstance trial) {
        super(CmdId.CurTrialActivityScNotify);

        var proto = CurTrialActivityScNotify.newInstance()
                .setTrialStageId(trial.getStageId());

        if (trial.isFinished()) {
            proto.setStatus(TrialActivityStatus.TRIAL_ACTIVITY_STATUS_FINISH);
        }

        this.setData(proto);
    }
}
