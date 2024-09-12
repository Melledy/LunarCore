package emu.lunarcore.server.packet.recv;

import emu.lunarcore.proto.FinishRogueCommonDialogueCsReqOuterClass.FinishRogueCommonDialogueCsReq;
import emu.lunarcore.server.game.GameSession;
import emu.lunarcore.server.packet.CmdId;
import emu.lunarcore.server.packet.Opcodes;
import emu.lunarcore.server.packet.PacketHandler;
import emu.lunarcore.server.packet.send.PacketSceneGroupRefreshScNotify;

@Opcodes(CmdId.FinishRogueCommonDialogueCsReq)
public class HandlerFinishRogueCommonDialogueCsReq extends PacketHandler {
    @Override
    public void handle(GameSession session, byte[] data) throws Exception {
        var req = FinishRogueCommonDialogueCsReq.parseFrom(data);

        var instance = session.getPlayer().getRogueInstance().getRunningEvents().get(req.getEventUniqueId());
        if (instance == null) return;
        instance.Finished = true;
        session.send(new PacketSceneGroupRefreshScNotify(instance.EventEntity, null));
        
        session.send(CmdId.FinishRogueCommonDialogueScRsp);
    }
}
