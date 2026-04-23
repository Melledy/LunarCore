package emu.lunarcore.server.packet.send;

import emu.lunarcore.proto.GetMainMissionCustomValueCsReqOuterClass.GetMainMissionCustomValueCsReq;
import emu.lunarcore.proto.GetMainMissionCustomValueScRspOuterClass.GetMainMissionCustomValueScRsp;
import emu.lunarcore.proto.MainMissionDataOuterClass.MainMissionData;
import emu.lunarcore.proto.MissionStatusOuterClass.MissionStatus;
import emu.lunarcore.server.packet.BasePacket;
import emu.lunarcore.server.packet.CmdId;

public class PacketGetMainMissionCustomValueScRsp extends BasePacket {

    public PacketGetMainMissionCustomValueScRsp(GetMainMissionCustomValueCsReq req) {
        super(CmdId.GetMainMissionCustomValueScRsp);

        var data = GetMainMissionCustomValueScRsp.newInstance();

        for (int missionId : req.getMainMissionIdList()) {
            var info = MainMissionData.newInstance()
                    .setId(missionId)
                    .setStatus(MissionStatus.MISSION_FINISH);
            
            data.addMainMissionList(info);
        }

        this.setData(data);
    }
}
