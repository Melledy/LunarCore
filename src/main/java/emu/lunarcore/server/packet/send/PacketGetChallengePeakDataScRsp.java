package emu.lunarcore.server.packet.send;

import emu.lunarcore.GameConstants;
import emu.lunarcore.proto.GetChallengePeakDataScRspOuterClass.GetChallengePeakDataScRsp;
import emu.lunarcore.server.packet.BasePacket;
import emu.lunarcore.server.packet.CmdId;

public class PacketGetChallengePeakDataScRsp extends BasePacket {

    public PacketGetChallengePeakDataScRsp() {
        super(CmdId.GetChallengePeakDataScRsp);

        var data = GetChallengePeakDataScRsp.newInstance()
                .setCurChallengePeakGroupId(GameConstants.CHALLENGE_PEAK_GROUP_ID);
        
        this.setData(data);
    }
}
