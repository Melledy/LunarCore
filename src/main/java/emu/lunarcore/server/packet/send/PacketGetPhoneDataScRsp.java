package emu.lunarcore.server.packet.send;

import emu.lunarcore.proto.GetPhoneDataScRspOuterClass.GetPhoneDataScRsp;
import emu.lunarcore.server.packet.BasePacket;
import emu.lunarcore.data.GameData;
import emu.lunarcore.server.packet.CmdId;
import emu.lunarcore.game.player.Player;

public class PacketGetPhoneDataScRsp extends BasePacket {

    public PacketGetPhoneDataScRsp(Player player) {
        super(CmdId.GetPhoneDataScRsp);

        var data = GetPhoneDataScRsp.newInstance()
            .setCurChatBubble(player.getChatBubble())
            .setCurPhoneTheme(player.getPhoneTheme());

        for (var chatBubble : GameData.getChatBubbleExcelMap().values()) {
            data.addOwnedChatBubbles(chatBubble.getId());
        }

        for (var phoneTheme : GameData.getPhoneThemeExcelMap().values()) {
            data.addOwnedPhoneThemes(phoneTheme.getId());
        }

        this.setData(data);
    }
}
