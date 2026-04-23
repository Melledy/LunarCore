package emu.lunarcore.server.packet.send;

import emu.lunarcore.LunarCore;
import emu.lunarcore.proto.PlayerHeartBeatScRspOuterClass.PlayerHeartBeatScRsp;
import emu.lunarcore.server.packet.BasePacket;
import emu.lunarcore.server.packet.CmdId;

public class PacketPlayerHeartBeatScRsp extends BasePacket {

    public PacketPlayerHeartBeatScRsp(long clientTime) {
        super(CmdId.PlayerHeartBeatScRsp);

        var data = PlayerHeartBeatScRsp.newInstance()
                .setClientTimeMs(clientTime)
                .setServerTimeMs(LunarCore.currentServerTime());

        this.setData(data);
    }
}
