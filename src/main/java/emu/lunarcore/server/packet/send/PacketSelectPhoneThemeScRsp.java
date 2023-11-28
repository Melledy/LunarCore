package emu.lunarcore.server.packet.send;

import emu.lunarcore.game.rogue.RogueMiracleData;
import emu.lunarcore.game.rogue.RogueMiracleSelectMenu;
import emu.lunarcore.proto.SelectPhoneThemeScRspOuterClass.SelectPhoneThemeScRsp;
import emu.lunarcore.server.packet.BasePacket;
import emu.lunarcore.server.packet.CmdId;
import emu.lunarcore.game.player.Player;

public class PacketSelectPhoneThemeScRsp extends BasePacket {

    public PacketSelectPhoneThemeScRsp(Player player, int themeId) {
        super(CmdId.SelectPhoneThemeScRsp);
        
        player.setPhoneTheme(themeId);

        var data = SelectPhoneThemeScRsp.newInstance()
            .setCurPhoneTheme(themeId); 
        
        this.setData(data);
    }
}
