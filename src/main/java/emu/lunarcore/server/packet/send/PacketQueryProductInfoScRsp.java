package emu.lunarcore.server.packet.send;

import emu.lunarcore.proto.QueryProductInfoScRspOuterClass.QueryProductInfoScRsp;
import emu.lunarcore.server.packet.BasePacket;
import emu.lunarcore.server.packet.CmdId;

public class PacketQueryProductInfoScRsp extends BasePacket {

    public PacketQueryProductInfoScRsp() {
        super(CmdId.QueryProductInfoScRsp);

        QueryProductInfoScRsp data = QueryProductInfoScRsp.newInstance()
                .setGiftCoinCurVersion(1)
                .setGiftCoinBoughtHistoryVersion(1)
                .setMonthCardOutdateTime(1999999999);

        this.setData(data);
    }
}
