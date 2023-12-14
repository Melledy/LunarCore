package emu.lunarcore.server.packet.send;

import emu.lunarcore.proto.RefreshTriggerByClientCsReqOuterClass.RefreshTriggerByClientCsReq;
import emu.lunarcore.proto.RefreshTriggerByClientScRspOuterClass.RefreshTriggerByClientScRsp;
import emu.lunarcore.server.packet.BasePacket;
import emu.lunarcore.server.packet.CmdId;

public class PacketRefreshTriggerByClientScRsp extends BasePacket {

    public PacketRefreshTriggerByClientScRsp(RefreshTriggerByClientCsReq req) {
        super(CmdId.RefreshTriggerByClientScRsp);
        
        var data = RefreshTriggerByClientScRsp.newInstance()
                .setTriggerEntityId(req.getTriggerEntityId())
                .setTriggerName(req.getTriggerName())
                .setRefreshTrigger(true);
        
        this.setData(data);
    }
}
