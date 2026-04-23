package emu.lunarcore.server.packet.recv;

import emu.lunarcore.data.GameData;
import emu.lunarcore.proto.FinishQuestCsReqOuterClass.FinishQuestCsReq;
import emu.lunarcore.server.game.GameSession;
import emu.lunarcore.server.packet.CmdId;
import emu.lunarcore.server.packet.Opcodes;
import emu.lunarcore.server.packet.PacketHandler;

@Opcodes(CmdId.FinishQuestCsReq)
public class HandlerFinishQuestCsReq extends PacketHandler {

    @Override
    public void handle(GameSession session, byte[] data) throws Exception {
        var req = FinishQuestCsReq.parseFrom(data);
        int rewardId = GameData.getQuestExcelMap().get(req.getQuestId()).getRewardID();
        var rewardExcel = GameData.getRewardExcelMap().get(rewardId);
        if (rewardExcel != null && GameData.getQuestExcelMap().get(req.getQuestId()).getQuestType() == 3) { // public const QuestType Cycle = 3;
            session.getPlayer().getInventory().addItems(rewardExcel.getRewards(), true);
        }
        session.send(CmdId.FinishQuestScRsp);
    }

}
