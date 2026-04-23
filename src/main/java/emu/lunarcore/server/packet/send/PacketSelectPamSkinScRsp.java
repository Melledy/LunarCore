package emu.lunarcore.server.packet.send;

import emu.lunarcore.game.player.Player;
import emu.lunarcore.proto.SelectPamSkinScRspOuterClass.SelectPamSkinScRsp;
import emu.lunarcore.server.packet.BasePacket;
import emu.lunarcore.server.packet.CmdId;

public class PacketSelectPamSkinScRsp extends BasePacket {

    public PacketSelectPamSkinScRsp(int pamSkinId, Player player) {
        super(CmdId.SelectPamSkinScRsp);

        player.setCurrentPamSkinId(pamSkinId);

        var data = SelectPamSkinScRsp.newInstance()
                // both need to be the same, idk why
                .setSelectPamSkinId(player.getCurrentPamSkinId())
                .setCurrentPamSkinId(player.getCurrentPamSkinId());

        this.setData(data);
    }
}
