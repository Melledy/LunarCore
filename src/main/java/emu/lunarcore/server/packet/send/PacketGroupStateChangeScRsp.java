package emu.lunarcore.server.packet.send;

import emu.lunarcore.proto.GroupStateChangeScNotifyOuterClass.GroupStateChangeScNotify;
import emu.lunarcore.proto.GroupStateInfoOuterClass.GroupStateInfo;
import emu.lunarcore.server.packet.BasePacket;
import emu.lunarcore.server.packet.CmdId;

public class PacketGroupStateChangeScRsp extends BasePacket {

    public PacketGroupStateChangeScRsp(GroupStateInfo groupInfo) {
        super(CmdId.GroupStateChangeScRsp);
        
        var data = GroupStateChangeScNotify.newInstance();
        data.setGroupStateInfo(groupInfo);
        
        this.setData(data);
    }
}
