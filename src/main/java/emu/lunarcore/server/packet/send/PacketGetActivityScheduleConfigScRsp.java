package emu.lunarcore.server.packet.send;

import emu.lunarcore.data.GameDepot;
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
        
        for (var activity : GameDepot.getActivityScheduleExcels()) {
            var info = ActivityScheduleInfo.newInstance()
                    .setActivityId(activity.getActivityId())
                    .setModuleId(activity.getModuleId())
                    .setBeginTime(activity.getBeginTime())
                    .setEndTime(activity.getEndTime());
            
            data.addActivityScheduleList(info);
        }
        
        this.setData(data);
    }
}
