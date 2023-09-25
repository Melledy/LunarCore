package emu.lunarcore.server.packet.send;

import emu.lunarcore.proto.GetMissionStatusCsReqOuterClass.GetMissionStatusCsReq;
import emu.lunarcore.proto.GetMissionStatusScRspOuterClass.GetMissionStatusScRsp;
import emu.lunarcore.proto.MissionOuterClass.Mission;
import emu.lunarcore.proto.MissionStatusOuterClass.MissionStatus;
import emu.lunarcore.server.packet.BasePacket;
import emu.lunarcore.server.packet.CmdId;

public class PacketGetMissionStatusScRsp extends BasePacket {

    public PacketGetMissionStatusScRsp(GetMissionStatusCsReq req) {
        super(CmdId.GetMissionStatusScRsp);

        var data = GetMissionStatusScRsp.newInstance();

        for (int missionId : req.getMainMissionIdList()) {
            data.addFinishedMainMissionIdList(missionId);
        }

        for (int missionId : req.getSubMissionIdList()) {
            var mission = Mission.newInstance()
                    .setId(missionId)
                    .setStatus(MissionStatus.MISSION_FINISH);

            data.addSubMissionStatusList(mission);
        }

        this.setData(data);
    }
}
