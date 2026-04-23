package emu.lunarcore.server.packet.send;

import emu.lunarcore.proto.DeleteSocialEventServerCacheCsReqOuterClass.DeleteSocialEventServerCacheCsReq;
import emu.lunarcore.proto.DeleteSocialEventServerCacheScRspOuterClass.DeleteSocialEventServerCacheScRsp;
import emu.lunarcore.server.packet.BasePacket;
import emu.lunarcore.server.packet.CmdId;

public class PacketDeleteSocialEventServerCacheScRsp extends BasePacket {

    public PacketDeleteSocialEventServerCacheScRsp(DeleteSocialEventServerCacheCsReq req) {
        super(CmdId.DeleteSocialEventServerCacheScRsp);

        var data = DeleteSocialEventServerCacheScRsp.newInstance();

        for (int cacheId : req.getDeleteCache()) {
            data.addRetDeleteCache(cacheId);
        }

        this.setData(data);
    }
}
