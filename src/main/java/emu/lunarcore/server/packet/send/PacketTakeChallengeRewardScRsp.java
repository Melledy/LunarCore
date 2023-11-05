package emu.lunarcore.server.packet.send;

import java.util.Collection;

import emu.lunarcore.game.inventory.GameItem;
import emu.lunarcore.proto.TakeChallengeRewardScRspOuterClass.TakeChallengeRewardScRsp;
import emu.lunarcore.server.packet.BasePacket;
import emu.lunarcore.server.packet.CmdId;

public class PacketTakeChallengeRewardScRsp extends BasePacket {

    public PacketTakeChallengeRewardScRsp(int groupId, int starCount, Collection<GameItem> rewards) {
        super(CmdId.TakeChallengeRewardScRsp);
        
        var data = TakeChallengeRewardScRsp.newInstance();
        
        if (rewards != null) {
            data.setGroupId(groupId)
                .setStarCount(starCount);
            
            for (GameItem item : rewards) {
                data.getMutableReward().addItemList(item.toProto());
            }
        } else {
            data.setRetcode(1);
        }  
        
        this.setData(data);
    }
}
