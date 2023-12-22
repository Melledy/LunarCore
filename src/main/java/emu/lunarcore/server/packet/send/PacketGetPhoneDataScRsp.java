package emu.lunarcore.server.packet.send;

import emu.lunarcore.proto.GetPhoneDataScRspOuterClass.GetPhoneDataScRsp;
import emu.lunarcore.server.packet.BasePacket;
import emu.lunarcore.server.packet.CmdId;
import emu.lunarcore.game.player.Player;

public class PacketGetPhoneDataScRsp extends BasePacket {

    public PacketGetPhoneDataScRsp(Player player) {
        super(CmdId.GetPhoneDataScRsp);

        var data = GetPhoneDataScRsp.newInstance()
            .setCurChatBubble(player.getChatBubble())
            .setCurPhoneTheme(player.getPhoneTheme());

        for (int chatBubbleId : player.getUnlocks().getChatBubbles()) {
            data.addOwnedChatBubbles(chatBubbleId);
        }

        for (int phoneThemeId : player.getUnlocks().getPhoneThemes()) {
            data.addOwnedPhoneThemes(phoneThemeId);
        }

        this.setData(data);
    }
}
