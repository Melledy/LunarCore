package emu.lunarcore.server.packet.send;

import emu.lunarcore.proto.GetBasicInfoScRspOuterClass.GetBasicInfoScRsp;
import emu.lunarcore.proto.PlayerSettingInfoOuterClass.PlayerSettingInfo;
import emu.lunarcore.server.game.GameSession;
import emu.lunarcore.server.packet.BasePacket;
import emu.lunarcore.server.packet.CmdId;

public class PacketGetBasicInfoScRsp extends BasePacket {

    public PacketGetBasicInfoScRsp(GameSession session) {
        super(CmdId.GetBasicInfoScRsp);

        var data = GetBasicInfoScRsp.newInstance()
                .setCurDay(1)
                .setNextRecoverTime(session.getPlayer().getNextStaminaRecover() / 1000)
                .setGameplayBirthday(session.getPlayer().getBirthday())
                .setPlayerSettingInfo(PlayerSettingInfo.newInstance());

        this.setData(data);
    }
}
