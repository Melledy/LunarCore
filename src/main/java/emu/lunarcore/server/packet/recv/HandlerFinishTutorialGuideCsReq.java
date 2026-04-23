package emu.lunarcore.server.packet.recv;

import emu.lunarcore.data.GameData;
import emu.lunarcore.proto.FinishTutorialGuideCsReqOuterClass.FinishTutorialGuideCsReq;
import emu.lunarcore.server.game.GameSession;
import emu.lunarcore.server.packet.CmdId;
import emu.lunarcore.server.packet.Opcodes;
import emu.lunarcore.server.packet.PacketHandler;
import emu.lunarcore.server.packet.send.PacketFinishTutorialGuideScRsp;

@Opcodes(CmdId.FinishTutorialGuideCsReq)
public class HandlerFinishTutorialGuideCsReq extends PacketHandler {

    @Override
    public void handle(GameSession session, byte[] data) throws Exception {
        var req = FinishTutorialGuideCsReq.parseFrom(data);
        
        var excel = GameData.getTutorialGuideExcelMap().get(req.getGroupId());
        
        session.send(new PacketFinishTutorialGuideScRsp(excel));
    }

}
