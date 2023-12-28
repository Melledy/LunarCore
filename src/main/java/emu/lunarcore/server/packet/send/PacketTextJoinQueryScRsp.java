package emu.lunarcore.server.packet.send;

import emu.lunarcore.data.GameData;
import emu.lunarcore.game.player.Player;
import emu.lunarcore.proto.TextJoinInfoOuterClass.TextJoinInfo;
import emu.lunarcore.proto.TextJoinQueryScRspOuterClass.TextJoinQueryScRsp;
import emu.lunarcore.server.packet.BasePacket;
import emu.lunarcore.server.packet.CmdId;

public class PacketTextJoinQueryScRsp extends BasePacket {

    public PacketTextJoinQueryScRsp(Player player, int[] textJoinIdList) {
        super(CmdId.TextJoinQueryScRsp);

        
        var data = TextJoinQueryScRsp.newInstance();

        for (int joinId : textJoinIdList) {
            TextJoinInfo joinInfo = TextJoinInfo.newInstance()
                .setTextItemId(joinId)
                .setTextItemConfigId(GameData.TextJoinItemFromId(joinId));
            data.addTextJoinList(joinInfo);
        }
        
        this.setData(data);
    }
}
