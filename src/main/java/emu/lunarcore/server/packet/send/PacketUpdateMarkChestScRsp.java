package emu.lunarcore.server.packet.send;

import java.util.List;

import emu.lunarcore.proto.MarkChestInfoOuterClass.MarkChestInfo;
import emu.lunarcore.proto.UpdateMarkChestScRspOuterClass.UpdateMarkChestScRsp;
import emu.lunarcore.server.packet.BasePacket;
import emu.lunarcore.server.packet.CmdId;

public class PacketUpdateMarkChestScRsp extends BasePacket {

    public PacketUpdateMarkChestScRsp(int funcId, int markAvatarId, List<MarkChestInfo> chestPropDefList) {
        super(CmdId.UpdateMarkChestScRsp);

        var data = UpdateMarkChestScRsp.newInstance()
                .setFuncId(funcId)
                .setMarkAvatarId(markAvatarId);

        for (MarkChestInfo chestPropDef : chestPropDefList) {
            data.addChestGroupList(chestPropDef);
        }

        this.setData(data);
    }
}
