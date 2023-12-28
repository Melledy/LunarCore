package emu.lunarcore.server.packet.send;

import emu.lunarcore.game.player.Player;
import emu.lunarcore.data.GameData;
import emu.lunarcore.proto.GetJukeboxDataScRspOuterClass.GetJukeboxDataScRsp;
import emu.lunarcore.proto.UnlockedMusicOuterClass.UnlockedMusic;
import emu.lunarcore.server.packet.BasePacket;
import emu.lunarcore.server.packet.CmdId;

public class PacketGetJukeboxDataScRsp extends BasePacket {

    public PacketGetJukeboxDataScRsp(Player player) {
        super(CmdId.GetJukeboxDataScRsp);

        var allmusicids = GameData.getAllMusicIds();

        var data = GetJukeboxDataScRsp.newInstance()
            .setPlayingId(player.getCurrentBgm());

        for (int musicId : allmusicids) {
            UnlockedMusic musicListEntry = UnlockedMusic.newInstance()
                .setId(musicId)
                .setUnkbool(true)
                .setGroupId(GameData.getMusicGroupId(musicId));

            data.addMusicList(musicListEntry);
        }

        this.setData(data);
    }
}
