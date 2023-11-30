package emu.lunarcore.server.packet.send;

import emu.lunarcore.proto.ServerAnnounceNotifyOuterClass.ServerAnnounceNotify;
import emu.lunarcore.proto.ServerAnnounceNotifyOuterClass.ServerAnnounceNotify.AnnounceData;
import emu.lunarcore.server.packet.BasePacket;
import emu.lunarcore.server.packet.CmdId;

public class PacketServerAnnounceNotify extends BasePacket {
    
    public PacketServerAnnounceNotify() {
        super(CmdId.ServerAnnounceNotify);

        AnnounceData announceData = AnnounceData.newInstance()
            .setBeginTime(0L)
            .setEndTime(9999999999999L)
            .setCountDownText("LUNARCORE IS A FREE SOFTWARE. IF YOU PAID FOR IT, YOU HAVE BEEN SCAMMED! lunarcore 是一款免费软件。如果你花钱买了它，那你就被骗了！")
            .setCenterSystemFrequency(100)
            .setCountDownFrequency(1)
            .setIsCenterSystemLast5EveryMinutes(true)
            .setConfigId(0);

        var data = ServerAnnounceNotify.newInstance()
            .addAnnounceDataList(announceData);

        this.setData(data);
    }
    
}