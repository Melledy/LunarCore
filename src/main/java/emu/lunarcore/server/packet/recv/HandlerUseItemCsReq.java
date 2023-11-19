package emu.lunarcore.server.packet.recv;

import java.util.List;

import emu.lunarcore.game.inventory.GameItem;
import emu.lunarcore.proto.UseItemCsReqOuterClass.UseItemCsReq;
import emu.lunarcore.server.game.GameSession;
import emu.lunarcore.server.packet.CmdId;
import emu.lunarcore.server.packet.Opcodes;
import emu.lunarcore.server.packet.PacketHandler;
import emu.lunarcore.server.packet.send.PacketUseItemScRsp;

@Opcodes(CmdId.UseItemCsReq)
public class HandlerUseItemCsReq extends PacketHandler {

    @Override
    public void handle(GameSession session, byte[] data) throws Exception {
        var req = UseItemCsReq.parseFrom(data);
        
        List<GameItem> returnItems = session.getPlayer().getInventory().useItem(req.getUseItemId(), req.getUseItemCount(), req.getBaseAvatarId());
        session.send(new PacketUseItemScRsp(req.getUseItemId(), req.getUseItemCount(), returnItems));
    }

}
