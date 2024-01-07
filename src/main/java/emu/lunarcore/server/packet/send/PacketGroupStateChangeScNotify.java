package emu.lunarcore.server.packet.send;

import emu.lunarcore.game.enums.PropState;
import emu.lunarcore.proto.GroupStateChangeScNotifyOuterClass.GroupStateChangeScNotify;
import emu.lunarcore.proto.GroupStateInfoOuterClass.GroupStateInfo;
import emu.lunarcore.server.packet.BasePacket;
import emu.lunarcore.server.packet.CmdId;

public class PacketGroupStateChangeScNotify extends BasePacket {

    public PacketGroupStateChangeScNotify(GroupStateInfo groupInfo) {
        super(CmdId.GroupStateChangeScNotify);
        
        var data = GroupStateChangeScNotify.newInstance();
        data.setGroupStateInfo(groupInfo);
        
        this.setData(data);
    }
    
    public PacketGroupStateChangeScNotify(int entryId, int groupId, PropState state) {
        super(CmdId.GroupStateChangeScNotify);
        
        var data = GroupStateChangeScNotify.newInstance();
        
        data.getMutableGroupStateInfo()
            .setEntryId(entryId)
            .setGroupId(groupId)
            .setGroupState(state.getVal());
        
        this.setData(data);
    }
}
