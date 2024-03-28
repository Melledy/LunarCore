package emu.lunarcore.server.packet.send;

import emu.lunarcore.proto.UpdateServerPrefsDataScRspOuterClass.UpdateServerPrefsDataScRsp;
import emu.lunarcore.server.packet.BasePacket;
import emu.lunarcore.server.packet.CmdId;

public class PacketUpdateServerPrefsDataScRsp extends BasePacket {

    public PacketUpdateServerPrefsDataScRsp(int id) {
        super(CmdId.UpdateServerPrefsDataScRsp);
        
        var data = UpdateServerPrefsDataScRsp.newInstance()
                .setServerPrefsId(id);
        
        this.setData(data);
    }
}
