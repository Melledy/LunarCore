package emu.lunarcore.server.packet.send;

import emu.lunarcore.data.GameData;
import emu.lunarcore.proto.ActivityScheduleInfoOuterClass.ActivityScheduleInfo;
import emu.lunarcore.proto.GetActivityScheduleConfigScRspOuterClass.GetActivityScheduleConfigScRsp;
import emu.lunarcore.server.packet.BasePacket;
import emu.lunarcore.server.packet.CacheablePacket;
import emu.lunarcore.server.packet.CmdId;

@CacheablePacket
public class PacketGetActivityScheduleConfigScRsp extends BasePacket {

    public PacketGetActivityScheduleConfigScRsp() {
        super(CmdId.GetActivityScheduleConfigScRsp);
        
        var data = GetActivityScheduleConfigScRsp.newInstance();
        
        for (var activity : GameData.getActivityPanelExcelMap().values()) {
            if (activity.getType() != 18) continue;
            
            var info = ActivityScheduleInfo.newInstance()
                    .setActivityId(activity.getPanelID())
                    .setModuleId(activity.getActivityModuleID())
                    .setBeginTime(0)
                    .setEndTime(Integer.MAX_VALUE);
            
            data.addActivityScheduleList(info);
        }
        
        this.setData(data);
    }
}
