package emu.lunarcore.server.packet.recv;

import java.util.List;

import emu.lunarcore.game.inventory.GameItem;
import emu.lunarcore.proto.TakeChallengeRewardCsReqOuterClass.TakeChallengeRewardCsReq;
import emu.lunarcore.server.game.GameSession;
import emu.lunarcore.server.packet.CmdId;
import emu.lunarcore.server.packet.Opcodes;
import emu.lunarcore.server.packet.PacketHandler;
import emu.lunarcore.server.packet.send.PacketTakeChallengeRewardScRsp;

@Opcodes(CmdId.TakeChallengeRewardCsReq)
public class HandlerTakeChallengeRewardCsReq extends PacketHandler {

    @Override
    public void handle(GameSession session, byte[] data) throws Exception {
        var req = TakeChallengeRewardCsReq.parseFrom(data);
        
        List<GameItem> rewards = session.getPlayer().getChallengeManager().takeRewards(req.getGroupId(), req.getStarCount());
        session.send(new PacketTakeChallengeRewardScRsp(req.getGroupId(), req.getStarCount(), rewards));
    }

}
