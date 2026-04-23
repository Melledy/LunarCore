package emu.lunarcore.server.packet.send;

import emu.lunarcore.game.player.Player;
import emu.lunarcore.proto.GetLineupAvatarDataScRspOuterClass.GetLineupAvatarDataScRsp;
import emu.lunarcore.proto.LineupAvatarDataOuterClass.LineupAvatarData;
import emu.lunarcore.server.packet.BasePacket;
import emu.lunarcore.server.packet.CmdId;

public class PacketGetLineupAvatarDataScRsp extends BasePacket {

    public PacketGetLineupAvatarDataScRsp(Player player) {
        super(CmdId.GetLineupAvatarDataScRsp);

        // Get current lineup
        var lineup = player.getCurrentLineup();
        
        // Set data
        var data = GetLineupAvatarDataScRsp.newInstance();
        
        lineup.forEachAvatar(avatar -> {
            var avatarData = LineupAvatarData.newInstance()
                    .setId(avatar.getAvatarId())
                    .setHp(avatar.getCurrentHp(lineup))
                    .setAvatarType(avatar.getAvatarType());
            
            data.addLineupAvatarDataList(avatarData);
        });
        
        this.setData(data);
    }
}
