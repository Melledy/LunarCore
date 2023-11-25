package emu.lunarcore.server.packet.recv;

import emu.lunarcore.proto.TakePromotionRewardCsReqOuterClass.TakePromotionRewardCsReq;
import emu.lunarcore.server.game.GameSession;
import emu.lunarcore.server.packet.CmdId;
import emu.lunarcore.server.packet.Opcodes;
import emu.lunarcore.server.packet.PacketHandler;
import emu.lunarcore.server.packet.send.PacketTakePromotionRewardScRsp;

@Opcodes(CmdId.TakePromotionRewardCsReq)
public class HandlerTakePromotionRewardCsReq extends PacketHandler {

    @Override
    public void handle(GameSession session, byte[] data) throws Exception {
        var req = TakePromotionRewardCsReq.parseFrom(data);
        
        var rewards = session.getServer().getInventoryService().takePromotionRewardAvatar(session.getPlayer(), req.getBaseAvatarId(), req.getPromotion());
        session.send(new PacketTakePromotionRewardScRsp(rewards));
    }

}
