package emu.lunarcore.server.packet.send;

import emu.lunarcore.GameConstants;
import emu.lunarcore.game.player.Player;
import emu.lunarcore.proto.ChatTypeOuterClass.ChatType;
import emu.lunarcore.proto.MsgTypeOuterClass.MsgType;
import emu.lunarcore.proto.RevcMsgScNotifyOuterClass.RevcMsgScNotify;
import emu.lunarcore.server.packet.BasePacket;
import emu.lunarcore.server.packet.CmdId;

public class PacketRevcMsgScNotify extends BasePacket {

    public PacketRevcMsgScNotify(Player player, String msg) {
        super(CmdId.RevcMsgScNotify);

        var data = RevcMsgScNotify.newInstance()
                .setText(msg)
                .setChatType(ChatType.CHAT_TYPE_PRIVATE)
                .setMsgType(MsgType.MSG_TYPE_CUSTOM_TEXT)
                .setFromUid(GameConstants.SERVER_CONSOLE_UID)
                .setToUid(player.getUid());

        this.setData(data);
    }
}
