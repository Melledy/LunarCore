package emu.lunarcore.server.packet.send;

import java.util.List;

import emu.lunarcore.proto.MarkChestChangedScNotifyOuterClass.MarkChestChangedScNotify;
import emu.lunarcore.proto.MarkChestInfoOuterClass.MarkChestInfo;
import emu.lunarcore.server.packet.BasePacket;
import emu.lunarcore.server.packet.CmdId;

public class PacketMarkChestChangedScNotify extends BasePacket {

    public PacketMarkChestChangedScNotify(List<MarkChestInfo> chestPropDefList) {
        super(CmdId.MarkChestChangedScNotify);

        var data = MarkChestChangedScNotify.newInstance();

        for (MarkChestInfo chestPropDef : chestPropDefList) {
            data.addChestGroupList(chestPropDef);
        }

        this.setData(data);
    }
}
