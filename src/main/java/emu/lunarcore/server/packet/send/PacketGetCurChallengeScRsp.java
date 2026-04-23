package emu.lunarcore.server.packet.send;

import emu.lunarcore.game.challenge.ChallengeInstance;
import emu.lunarcore.game.player.Player;
import emu.lunarcore.proto.GetCurChallengeScRspOuterClass.GetCurChallengeScRsp;
import emu.lunarcore.server.packet.BasePacket;
import emu.lunarcore.server.packet.CmdId;

public class PacketGetCurChallengeScRsp extends BasePacket {

    public PacketGetCurChallengeScRsp(Player player) {
        super(CmdId.GetCurChallengeScRsp);

        var data = GetCurChallengeScRsp.newInstance();

        ChallengeInstance instance = player.getInstance(ChallengeInstance.class);
        if (instance != null) {
            data.setChallengeInfo(instance.toProto());
        } else {
            data.setRetcode(0);
        }

        this.setData(data);
    }
}
