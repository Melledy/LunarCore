package emu.lunarcore.server.packet.send;

import emu.lunarcore.proto.GetMainMissionCustomValueScRspOuterClass.GetMainMissionCustomValueScRsp;
import emu.lunarcore.proto.MainMissionOuterClass.MainMission;
import emu.lunarcore.proto.MissionStatusOuterClass.MissionStatus;
import emu.lunarcore.server.packet.BasePacket;
import emu.lunarcore.server.packet.CmdId;
import us.hebi.quickbuf.RepeatedInt;

public class PacketGetMainMissionCustomValueScRsp extends BasePacket {

    public PacketGetMainMissionCustomValueScRsp(RepeatedInt list) {
        super(CmdId.GetMainMissionCustomValueScRsp);
        
        var data = GetMainMissionCustomValueScRsp.newInstance();

        for (int mainMissionId : list) {
            MainMission mainMission = MainMission.newInstance()
                .setId(mainMissionId)
                .setStatus(MissionStatus.MISSION_FINISH);
            
            data.addMainMissionList(mainMission);
        }

        this.setData(data);
    }
}
