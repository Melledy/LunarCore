package emu.lunarcore.server.packet.send;

import emu.lunarcore.game.player.Player;
import emu.lunarcore.proto.GetFriendLoginInfoScRspOuterClass.GetFriendLoginInfoScRsp;
import emu.lunarcore.server.packet.BasePacket;
import emu.lunarcore.server.packet.CmdId;

public class PacketGetFriendLoginInfoScRsp extends BasePacket {

    public PacketGetFriendLoginInfoScRsp(Player player) {
        super(CmdId.GetFriendLoginInfoScRsp);
        
        var data = GetFriendLoginInfoScRsp.newInstance()
                .addAllFriendUidList(player.getFriendList().toFriendUidArray());
        
        this.setData(data);
    }
}
