package emu.lunarcore.server.packet.send;

import emu.lunarcore.game.player.Player;
import emu.lunarcore.proto.GetSocialEventServerCacheScRspOuterClass.GetSocialEventServerCacheScRsp;
import emu.lunarcore.proto.SocialEventServerCacheOuterClass.SocialEventServerCache;
import emu.lunarcore.server.packet.BasePacket;
import emu.lunarcore.server.packet.CmdId;

public class PacketGetSocialEventServerCacheScRsp extends BasePacket {

    public PacketGetSocialEventServerCacheScRsp(Player player) {
        super(CmdId.GetSocialEventServerCacheScRsp);

        var socialevent = SocialEventServerCache.newInstance()
                .setSrcUid(player.getUid())
                .setAddCoin(10000)
                .setId(100);

        var data = GetSocialEventServerCacheScRsp.newInstance()
                .addSocialEvent(socialevent);

        this.setData(data);
    }
}
