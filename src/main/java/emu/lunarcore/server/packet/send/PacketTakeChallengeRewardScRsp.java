package emu.lunarcore.server.packet.send;

import java.util.Collection;

import emu.lunarcore.proto.TakeChallengeRewardScRspOuterClass.TakeChallengeRewardScRsp;
import emu.lunarcore.proto.TakenChallengeRewardInfoOuterClass.TakenChallengeRewardInfo;
import emu.lunarcore.server.packet.BasePacket;
import emu.lunarcore.server.packet.CmdId;

public class PacketTakeChallengeRewardScRsp extends BasePacket {

    public PacketTakeChallengeRewardScRsp(int groupId, Collection<TakenChallengeRewardInfo> rewards) {
        super(CmdId.TakeChallengeRewardScRsp);
        
        var data = TakeChallengeRewardScRsp.newInstance();
        
        if (rewards != null) {
            data.setGroupId(groupId);
            
            for (var rewardInfo : rewards) {
                data.getMutableTakenRewardList().add(rewardInfo);
            }
        } else {
            data.setRetcode(1);
        }  
        
        this.setData(data);
    }
}
