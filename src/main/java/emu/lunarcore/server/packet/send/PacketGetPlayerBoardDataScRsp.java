package emu.lunarcore.server.packet.send;

import emu.lunarcore.game.player.Player;
import emu.lunarcore.proto.GetPlayerBoardDataScRspOuterClass.GetPlayerBoardDataScRsp;
import emu.lunarcore.proto.HeadIconOuterClass.HeadIcon;
import emu.lunarcore.server.packet.BasePacket;
import emu.lunarcore.server.packet.CmdId;

public class PacketGetPlayerBoardDataScRsp extends BasePacket {

    public PacketGetPlayerBoardDataScRsp(Player player) {
        super(CmdId.GetPlayerBoardDataScRsp);
        
        var data = GetPlayerBoardDataScRsp.newInstance()
                .setCurrentHeadIconId(player.getHeadIcon())
                .setSignature(player.getSignature());
        
        // Set empty display avatars
        data.getMutableDisplayAvatarVec();
        
        for (int id : player.getUnlocks().getHeadIcons()) {
            data.addUnlockedHeadIconList(HeadIcon.newInstance().setId(id));
        }
        
        this.setData(data);
    }
}
