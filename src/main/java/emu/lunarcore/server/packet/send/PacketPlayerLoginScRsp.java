package emu.lunarcore.server.packet.send;

import emu.lunarcore.GameConstants;
import emu.lunarcore.LunarCore;
import emu.lunarcore.proto.PlayerLoginScRspOuterClass.PlayerLoginScRsp;
import emu.lunarcore.server.game.GameSession;
import emu.lunarcore.server.packet.BasePacket;
import emu.lunarcore.server.packet.CmdId;

public class PacketPlayerLoginScRsp extends BasePacket {

    public PacketPlayerLoginScRsp(GameSession session) {
        super(CmdId.PlayerLoginScRsp);

        var data = PlayerLoginScRsp.newInstance()
                .setBasicInfo(session.getPlayer().toProto())
                .setCurTimezone(GameConstants.CURRENT_TIMEZONE)
                .setServerTimestampMs(LunarCore.currentServerTime())
                .setStamina(session.getPlayer().getStamina());

        this.setData(data);
    }
}
