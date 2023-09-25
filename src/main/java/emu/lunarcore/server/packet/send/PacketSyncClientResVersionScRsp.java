package emu.lunarcore.server.packet.send;

import emu.lunarcore.proto.SyncClientResVersionScRspOuterClass.SyncClientResVersionScRsp;
import emu.lunarcore.server.packet.BasePacket;
import emu.lunarcore.server.packet.CmdId;

public class PacketSyncClientResVersionScRsp extends BasePacket {

    public PacketSyncClientResVersionScRsp(int res) {
        super(CmdId.SyncClientResVersionScRsp);

        var data = SyncClientResVersionScRsp.newInstance()
                .setClientResVersion(res);

        this.setData(data);
    }
}