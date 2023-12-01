package emu.lunarcore.server.packet.send;

import emu.lunarcore.proto.GetMainMissionCustomValueScRspOuterClass.GetMainMissionCustomValueScRsp;
import emu.lunarcore.proto.MainMissionOuterClass.MainMission;
import emu.lunarcore.server.packet.BasePacket;
import emu.lunarcore.server.packet.CmdId;

public class PacketGetMainMissionCustomValueScRsp extends BasePacket {

    // TODO cache packet
    public PacketGetMainMissionCustomValueScRsp(int[] mainMissionIdList) {
        super(CmdId.GetMainMissionCustomValueScRsp);
        
        var data = GetMainMissionCustomValueScRsp.newInstance();

        for (int mainMissionId : mainMissionIdList) {
            MainMission mainMission = MainMission.newInstance()
                .setId(mainMissionId);
            data.addMainMissionList(mainMission);
        }

        this.setData(data);
    }
}
