package emu.lunarcore.server.packet.send;

import emu.lunarcore.proto.FeatureSwitchInfoOuterClass.FeatureSwitchInfo;
import emu.lunarcore.proto.FeatureSwitchParamOuterClass.FeatureSwitchParam;
import emu.lunarcore.proto.FeatureSwitchTypeOuterClass.FeatureSwitchType;
import emu.lunarcore.proto.UpdateFeatureSwitchScNotifyOuterClass.UpdateFeatureSwitchScNotify;
import emu.lunarcore.server.packet.BasePacket;
import emu.lunarcore.server.packet.CmdId;

public class PacketUpdateFeatureSwitchScNotify extends BasePacket {

    public PacketUpdateFeatureSwitchScNotify() {
        super(CmdId.UpdateFeatureSwitchScNotify);

        var data = UpdateFeatureSwitchScNotify.newInstance();

        var switch1 = FeatureSwitchInfo.newInstance()
                .setType(FeatureSwitchType.FEATURE_SWITCH_ACTIVITY_SCHEDULE)
                .addAllSwitchList(FeatureSwitchParam.newInstance().addSwitchId(550002));

        data.addSwitchInfoList(switch1);

        this.setData(data);
    }
}
