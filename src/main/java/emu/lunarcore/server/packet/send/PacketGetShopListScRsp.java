package emu.lunarcore.server.packet.send;

import emu.lunarcore.proto.GetShopListScRspOuterClass.GetShopListScRsp;
import emu.lunarcore.server.packet.BasePacket;
import emu.lunarcore.server.packet.CmdId;

public class PacketGetShopListScRsp extends BasePacket {

    public PacketGetShopListScRsp(int shopType) {
        super(CmdId.GetShopListScRsp);

        var data = GetShopListScRsp.newInstance()
                .setShopType(shopType);
        
        this.setData(data);
    }
}
