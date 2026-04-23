package emu.lunarcore.server.packet.send;

import emu.lunarcore.data.GameData;
import emu.lunarcore.game.player.Player;
import emu.lunarcore.proto.GetPamSkinDataScRspOuterClass.GetPamSkinDataScRsp;
import emu.lunarcore.server.packet.BasePacket;
import emu.lunarcore.server.packet.CmdId;

public class PacketGetPamSkinDataScRsp extends BasePacket {

    public PacketGetPamSkinDataScRsp(Player player) {
        super(CmdId.GetPamSkinDataScRsp);

        // Hack for existing accounts
        if (player.getCurrentPamSkinId() == 0) {
            player.setCurrentPamSkinId(252000);
        }

        var pamSkinsList = GameData.getPomSkinExcelMap().getIds();

        var data = GetPamSkinDataScRsp.newInstance()
                .addAllUnlockedPamSkins(pamSkinsList.intStream().toArray())
                .setCurrentPamSkinId(player.getCurrentPamSkinId());

        this.setData(data);
    }
}
