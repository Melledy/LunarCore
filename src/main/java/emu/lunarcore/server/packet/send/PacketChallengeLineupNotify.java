package emu.lunarcore.server.packet.send;

import emu.lunarcore.proto.ChallengeLineupNotifyOuterClass.ChallengeLineupNotify;
import emu.lunarcore.server.packet.BasePacket;
import emu.lunarcore.server.packet.CmdId;

public class PacketChallengeLineupNotify extends BasePacket {

    public PacketChallengeLineupNotify(int type) {
        super(CmdId.ChallengeLineupNotify);
        
        var data = ChallengeLineupNotify.newInstance()
                .setExtraLineupTypeValue(type);
        
        this.setData(data);
    }
}
