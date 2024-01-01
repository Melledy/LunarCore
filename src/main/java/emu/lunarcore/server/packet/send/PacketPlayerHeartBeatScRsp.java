package emu.lunarcore.server.packet.send;

import emu.lunarcore.LunarCore;
import emu.lunarcore.proto.PlayerHeartbeatScRspOuterClass.PlayerHeartbeatScRsp;
import emu.lunarcore.server.packet.BasePacket;
import emu.lunarcore.server.packet.CmdId;

public class PacketPlayerHeartBeatScRsp extends BasePacket {

    public PacketPlayerHeartBeatScRsp(long clientTime) {
        super(CmdId.PlayerHeartBeatScRsp);

        var data = PlayerHeartbeatScRsp.newInstance()
                .setClientTimeMs(clientTime)
                .setServerTimeMs(LunarCore.currentServerTime());

        this.setData(data);
    }
}
