package emu.lunarcore.server.packet.send;

import java.util.List;

import emu.lunarcore.proto.GetMarkChestScRspOuterClass.GetMarkChestScRsp;
import emu.lunarcore.proto.MarkChestInfoOuterClass.MarkChestInfo;
import emu.lunarcore.server.packet.BasePacket;
import emu.lunarcore.server.packet.CmdId;

public class PacketGetMarkChestScRsp extends BasePacket {

    public PacketGetMarkChestScRsp(List<MarkChestInfo> chestInfos) {
        super(CmdId.GetMarkChestScRsp);

        var data = GetMarkChestScRsp.newInstance();

        for (MarkChestInfo chestInfo : chestInfos) {
            data.addChestGroupList(chestInfo);
        }


        this.setData(data);
    }
}
