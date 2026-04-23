package emu.lunarcore.server.packet.send;

import emu.lunarcore.game.player.Player;
import emu.lunarcore.proto.GetBasicInfoScRspOuterClass.GetBasicInfoScRsp;
import emu.lunarcore.server.packet.BasePacket;
import emu.lunarcore.server.packet.CmdId;

public class PacketGetBasicInfoScRsp extends BasePacket {

    public PacketGetBasicInfoScRsp(Player player) {
        super(CmdId.GetBasicInfoScRsp);

        var data = GetBasicInfoScRsp.newInstance()
                .setCurDay(1)
                .setIsGenderSet(true)
                .setGender(player.getGender().getVal())
                .setWeekCocoonFinishedCount(0)
                .setNextRecoverTime(player.getNextStaminaRecover() / 1000)
                .setGameplayBirthday(player.getBirthday());
        
        var settings = data.getMutablePlayerSettingInfo();
        settings.getMutableUnk1();
        settings.getMutableUnk2();

        this.setData(data);
    }
}
