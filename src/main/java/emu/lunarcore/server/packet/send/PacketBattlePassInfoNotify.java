package emu.lunarcore.server.packet.send;

import emu.lunarcore.proto.BattlePassInfoNotifyOuterClass.BattlePassInfoNotify;
import emu.lunarcore.proto.BattlePassInfoNotifyOuterClass.BattlePassInfoNotify.BpTierType;
import emu.lunarcore.server.packet.BasePacket;
import emu.lunarcore.server.packet.CmdId;

public class PacketBattlePassInfoNotify extends BasePacket {

    public PacketBattlePassInfoNotify() {
        super(CmdId.BattlePassInfoNotify);

        var data = BattlePassInfoNotify.newInstance()
            .setTakenFreeReward(0xFFFFFFFFFFFFFFFFL)
            .setTakenPremiumReward1(0xFFFFFFFFFFFFFFFFL)
            .setTakenPremiumReward2(0x7FFFFFFFFFFFEL)
            .setTakenPremiumOptionalReward(0x7FFFFFFFFFFFEL)
            .setTakenFreeExtendedReward(127)
            .setTakenPremiumExtendedReward(127)
            .setUnkfield(4)
            .setLevel(70)
            .setCurWeekAddExpSum(8000)
            .setExp(800)
            .setCurBpId(5) // doesn't matter
            .setBpTierType(BpTierType.BP_TIER_TYPE_PREMIUM_2);
        
        this.setData(data);
    }
}
