package emu.lunarcore.server.packet.send;

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

        // Trials TODO
        addActivity(data, 20027, 2002700);
        addActivity(data, 20027, 2002501);
        addActivity(data, 20027, 2002502);
        addActivity(data, 20027, 2002601);
        addActivity(data, 20030, 2003000);
        addActivity(data, 20030, 2002801);
        addActivity(data, 20030, 2002901);
        addActivity(data, 20030, 2002803);
        addActivity(data, 20033, 2003300);
        addActivity(data, 20033, 2003101);
        addActivity(data, 20033, 2003201);
        addActivity(data, 20036, 2003600);
        addActivity(data, 20036, 2003401);
        addActivity(data, 20036, 2003501);
        addActivity(data, 20039, 2003900);
        addActivity(data, 20039, 2003701);
        addActivity(data, 20039, 2003801);
        addActivity(data, 20042, 2004200);
        addActivity(data, 20042, 2004001);
        addActivity(data, 20042, 2004101);
        addActivity(data, 20042, 2004002);
        addActivity(data, 20046, 2004401);
        addActivity(data, 20049, 2004701);
        addActivity(data, 20049, 2004801);
        addActivity(data, 20052, 2005001);
        addActivity(data, 20052, 2005101);
        addActivity(data, 20053, 2008007);
        addActivity(data, 20056, 2005401);
        addActivity(data, 20056, 2005501);
        addActivity(data, 20056, 2005501);
        
        // Add proto
        this.setData(data);
    }

    private void addActivity(GetActivityScheduleConfigScRsp data, int panelId, int moduleId) {
        var info = ActivityScheduleInfo.newInstance()
                .setActivityId(panelId)
                .setModuleId(moduleId)
                .setBeginTime(0)
                .setEndTime(Integer.MAX_VALUE);

        data.addActivityScheduleList(info);
    }
}
