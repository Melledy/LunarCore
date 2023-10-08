package emu.lunarcore.server.packet.send;

import emu.lunarcore.proto.ChallengeLineupNotifyOuterClass.ChallengeLineupNotify;
import emu.lunarcore.proto.ExtraLineupTypeOuterClass.ExtraLineupType;
import emu.lunarcore.server.packet.BasePacket;
import emu.lunarcore.server.packet.CmdId;

public class PacketChallengeLineupNotify extends BasePacket {

    public PacketChallengeLineupNotify(ExtraLineupType type) {
        super(CmdId.ChallengeLineupNotify);
        
        var data = ChallengeLineupNotify.newInstance()
                .setExtraLineupType(type);
        
        this.setData(data);
    }
}
