package emu.lunarcore.server.packet.send;

import java.util.Collection;

import emu.lunarcore.game.player.Player;
import emu.lunarcore.proto.FriendRecommendInfoOuterClass.FriendRecommendInfo;
import emu.lunarcore.proto.GetFriendRecommendListInfoScRspOuterClass.GetFriendRecommendListInfoScRsp;
import emu.lunarcore.server.packet.BasePacket;
import emu.lunarcore.server.packet.CmdId;

public class PacketGetFriendRecommendListInfoScRsp extends BasePacket {

    public PacketGetFriendRecommendListInfoScRsp(Collection<Player> list) {
        super(CmdId.GetFriendRecommendListInfoScRsp);
        
        var data = GetFriendRecommendListInfoScRsp.newInstance();
        
        for (Player player : list) {
            var info = FriendRecommendInfo.newInstance()
                    .setSimpleInfo(player.toSimpleInfo());
            
            data.addFriendRecommendList(info);
        }
        
        this.setData(data);
    }
}
