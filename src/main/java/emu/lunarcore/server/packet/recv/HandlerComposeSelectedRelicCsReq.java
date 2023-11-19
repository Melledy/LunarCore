package emu.lunarcore.server.packet.recv;

import java.util.List;

import emu.lunarcore.game.inventory.GameItem;
import emu.lunarcore.proto.ComposeSelectedRelicCsReqOuterClass.ComposeSelectedRelicCsReq;
import emu.lunarcore.server.game.GameSession;
import emu.lunarcore.server.packet.CmdId;
import emu.lunarcore.server.packet.Opcodes;
import emu.lunarcore.server.packet.PacketHandler;
import emu.lunarcore.server.packet.send.PacketComposeSelectedRelicScRsp;

@Opcodes(CmdId.ComposeSelectedRelicCsReq)
public class HandlerComposeSelectedRelicCsReq extends PacketHandler {

    @Override
    public void handle(GameSession session, byte[] data) throws Exception {
        var req = ComposeSelectedRelicCsReq.parseFrom(data);
        
        List<GameItem> returnList = session.getServer().getInventoryService().composeRelic(
                session.getPlayer(), 
                req.getComposeId(), 
                req.getComposeRelicId(),
                req.getMainAffixId(),
                req.getCount()
        );
        
        session.send(new PacketComposeSelectedRelicScRsp(req.getComposeId(), returnList));
    }

}
