package emu.lunarcore.server.packet.send;

import emu.lunarcore.game.player.Player;
import emu.lunarcore.proto.FriendListInfoOuterClass.FriendListInfo;
import emu.lunarcore.proto.HandleFriendScRspOuterClass.HandleFriendScRsp;
import emu.lunarcore.server.packet.BasePacket;
import emu.lunarcore.server.packet.CmdId;

public class PacketHandleFriendScRsp extends BasePacket {

    public PacketHandleFriendScRsp(Player friend, boolean result) {
        super(CmdId.HandleFriendScRsp);
        
        var data = HandleFriendScRsp.newInstance()
                .setUid(friend.getUid())
                .setHandleResult(result);
        
        if (result) {
            data.setHandleFriendInfo(FriendListInfo.newInstance().setSimpleInfo(friend.toSimpleInfo()));
        }
        
        this.setData(data);
    }
}
