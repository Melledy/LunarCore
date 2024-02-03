package emu.lunarcore.server.packet.send;

import emu.lunarcore.data.GameData;
import emu.lunarcore.game.player.Player;
import emu.lunarcore.proto.ChessRogueNousDiceInfoOuterClass.ChessRogueNousDiceInfo;
import emu.lunarcore.proto.ChessRogueNousDiceSurfaceInfoOuterClass.ChessRogueNousDiceSurfaceInfo;
import emu.lunarcore.proto.ChessRogueNousQueryInfoOuterClass.ChessRogueNousQueryInfo;
import emu.lunarcore.proto.ChessRogueQueryOuterClass.ChessRogueQuery;
import emu.lunarcore.proto.ChessRogueQueryScRspOuterClass.ChessRogueQueryScRsp;
import emu.lunarcore.server.packet.BasePacket;
import emu.lunarcore.server.packet.CmdId;

public class PacketChessRogueQueryScRsp extends BasePacket {
    public PacketChessRogueQueryScRsp(Player player) {
        super(CmdId.ChessRogueQueryScRsp);
        
        var data = ChessRogueQuery.newInstance();
        
        for (var entry: GameData.getRogueNousMainStoryExcelMap().keySet()) {
            data.addMainStoryId(entry);
        }
        player.getChessRogueManager().getRogueDefaultDice().forEach((k, v) -> {
            var dice = ChessRogueNousDiceInfo.newInstance()
                .setDiceBranchId(k);
            var index = 0;
            for (Integer d : v) {
                dice.addDiceSurface(ChessRogueNousDiceSurfaceInfo.newInstance()
                    .setIndex(++index)
                    .setDiceId(d));
            }
            data.addDiceInfo(dice);
        });
        var proto = ChessRogueQueryScRsp.newInstance()
            .setRogueNous(ChessRogueNousQueryInfo.newInstance()
                .setQueryInfo(data));
        
        this.setData(proto);
    }
}
