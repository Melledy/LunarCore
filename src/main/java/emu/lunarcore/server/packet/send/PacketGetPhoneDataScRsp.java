package emu.lunarcore.server.packet.send;

import emu.lunarcore.proto.GetPhoneDataScRspOuterClass.GetPhoneDataScRsp;
import emu.lunarcore.server.game.GameSession;
import emu.lunarcore.server.packet.BasePacket;
import emu.lunarcore.data.GameData;
import emu.lunarcore.server.packet.CmdId;
import emu.lunarcore.game.player.Player;

public class PacketGetPhoneDataScRsp extends BasePacket {

    public PacketGetPhoneDataScRsp(Player player) {
        super(CmdId.GetPhoneDataScRsp);

        var allChatBubbles = GameData.getAllChatBubbleIds();
        var allPhoneThemes = GameData.getAllPhoneThemes();

        var data = GetPhoneDataScRsp.newInstance()
            .setCurChatBubble(player.getChatBubble())
            .setCurPhoneTheme(player.getPhoneTheme());

        for (int chatBubbleId : allChatBubbles) {
            data.addOwnedChatBubbles(chatBubbleId);
        }

        for (int phoneThemeId : allPhoneThemes) {
            data.addOwnedPhoneThemes(phoneThemeId);
        }

        this.setData(data);
    }
}
