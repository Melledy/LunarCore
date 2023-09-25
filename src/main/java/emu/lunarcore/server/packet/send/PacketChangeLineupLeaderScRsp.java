package emu.lunarcore.server.packet.send;

import emu.lunarcore.proto.ChangeLineupLeaderScRspOuterClass.ChangeLineupLeaderScRsp;
import emu.lunarcore.server.packet.BasePacket;
import emu.lunarcore.server.packet.CmdId;

public class PacketChangeLineupLeaderScRsp extends BasePacket {

    public PacketChangeLineupLeaderScRsp(int slot) {
        super(CmdId.ChangeLineupLeaderScRsp);

        var data = ChangeLineupLeaderScRsp.newInstance()
                .setSlot(slot);

        this.setData(data);
    }
}
