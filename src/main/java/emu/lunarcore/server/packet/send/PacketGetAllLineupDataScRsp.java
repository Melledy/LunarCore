package emu.lunarcore.server.packet.send;

import emu.lunarcore.game.player.lineup.LineupManager;
import emu.lunarcore.game.player.lineup.PlayerLineup;
import emu.lunarcore.proto.GetAllLineupDataScRspOuterClass.GetAllLineupDataScRsp;
import emu.lunarcore.server.game.GameSession;
import emu.lunarcore.server.packet.BasePacket;
import emu.lunarcore.server.packet.CmdId;

public class PacketGetAllLineupDataScRsp extends BasePacket {

    public PacketGetAllLineupDataScRsp(GameSession session) {
        super(CmdId.GetAllLineupDataScRsp);

        LineupManager lineupManager = session.getPlayer().getLineupManager();

        var data = GetAllLineupDataScRsp.newInstance()
                .setCurIndex(session.getPlayer().getLineupManager().getCurrentIndex());

        for (PlayerLineup lineup : lineupManager.getLineups()) {
            data.addLineupList(lineup.toProto());
        }

        this.setData(data);
    }
}
