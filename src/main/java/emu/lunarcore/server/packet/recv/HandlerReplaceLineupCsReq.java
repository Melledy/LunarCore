package emu.lunarcore.server.packet.recv;

import java.util.ArrayList;
import java.util.List;

import emu.lunarcore.proto.LineupSlotDataOuterClass.LineupSlotData;
import emu.lunarcore.proto.ReplaceLineupCsReqOuterClass.ReplaceLineupCsReq;
import emu.lunarcore.server.game.GameSession;
import emu.lunarcore.server.packet.CmdId;
import emu.lunarcore.server.packet.Opcodes;
import emu.lunarcore.server.packet.PacketHandler;

@Opcodes(CmdId.ReplaceLineupCsReq)
public class HandlerReplaceLineupCsReq extends PacketHandler {

    @Override
    public void handle(GameSession session, byte[] data) throws Exception {
        var req = ReplaceLineupCsReq.parseFrom(data);

        List<Integer> lineupList = new ArrayList<>(req.getSlots().length());
        for (LineupSlotData slot : req.getSlots()) {
            lineupList.add(slot.getId());
        }

        session.getPlayer().getLineupManager().replaceLineup(req.getIndex(), req.getExtraLineupTypeValue(), lineupList);
        session.send(CmdId.ReplaceLineupScRsp);
    }

}
