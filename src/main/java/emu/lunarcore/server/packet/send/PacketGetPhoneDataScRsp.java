package emu.lunarcore.server.packet.send;

import emu.lunarcore.game.player.Player;
import emu.lunarcore.proto.GetPhoneDataScRspOuterClass.GetPhoneDataScRsp;
import emu.lunarcore.server.packet.BasePacket;
import emu.lunarcore.server.packet.CmdId;

public class PacketGetPhoneDataScRsp extends BasePacket {

    public PacketGetPhoneDataScRsp(Player player) {
        super(CmdId.GetPhoneDataScRsp);

        var data = GetPhoneDataScRsp.newInstance()
                .setCurChatBubble(player.getChatBubble())
                .setCurPhoneTheme(player.getPhoneTheme())
                .setCurPhoneCase(player.getPhoneCase());

        for (int chatBubbleId : player.getUnlocks().getChatBubbles()) {
            data.addOwnedChatBubbles(chatBubbleId);
        }

        for (int phoneThemeId : player.getUnlocks().getPhoneThemes()) {
            data.addOwnedPhoneThemes(phoneThemeId);
        }
        
        for (int phoneCaseId : player.getUnlocks().getPhoneCases()) {
            data.addOwnedPhoneCases(phoneCaseId);
        }

        this.setData(data);
    }
}
