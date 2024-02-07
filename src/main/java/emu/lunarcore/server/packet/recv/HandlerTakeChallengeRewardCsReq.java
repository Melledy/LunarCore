package emu.lunarcore.server.packet.recv;

import java.util.List;

import emu.lunarcore.proto.TakeChallengeRewardCsReqOuterClass.TakeChallengeRewardCsReq;
import emu.lunarcore.proto.TakenChallengeRewardInfoOuterClass.TakenChallengeRewardInfo;
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
        
        List<TakenChallengeRewardInfo> rewardInfos = session.getPlayer().getChallengeManager().takeRewards(req.getGroupId());
        session.send(new PacketTakeChallengeRewardScRsp(req.getGroupId(), rewardInfos));
    }

}
