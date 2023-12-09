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
			
			if (activity.getType() != 30) { //狐斋志异
				if (activity.getType() != 2) { //巡星之礼
					if (activity.getType() != 22) { //金人旧巷市廛喧
						if (activity.getType() != 16) { //冬城博物珍奇簿
							if (activity.getType() != 27) { //以太战线
								if (activity.getType() != 41) { //磐岩镇斗技表演赛
									if (activity.getType() != 31) { //模拟宇宙·寰宇蝗灾
										if (activity.getType() != 11) { //模拟宇宙
											continue;
										}
									}
								}
							}
						}
					}
				}
			}
            
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
