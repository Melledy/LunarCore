package emu.lunarcore.server.packet.send;

import java.util.List;
import emu.lunarcore.proto.UnlockBackGroundMusicScRspOuterClass.UnlockBackGroundMusicScRsp;
import emu.lunarcore.proto.UnlockedMusicOuterClass.UnlockedMusic;
import emu.lunarcore.server.packet.BasePacket;
import emu.lunarcore.server.packet.CmdId;
import emu.lunarcore.data.GameData;

public class PacketUnlockBackGroundMusicScRsp extends BasePacket {

    public PacketUnlockBackGroundMusicScRsp(List<Integer> unlockIds) {
        super(CmdId.UnlockBackGroundMusicScRsp);

        var data = UnlockBackGroundMusicScRsp.newInstance();

        for (int unlockId : unlockIds) {
            UnlockedMusic music = UnlockedMusic.newInstance()
                .setGroupId(GameData.getMusicGroupId(unlockId))
                .setId(unlockId);
            data.addMusicList(music);
        }
        
        this.setData(data);
    }

    public PacketUnlockBackGroundMusicScRsp() {
        super(CmdId.UnlockBackGroundMusicScRsp);
        this.setData(UnlockBackGroundMusicScRsp.newInstance());
    }
}
